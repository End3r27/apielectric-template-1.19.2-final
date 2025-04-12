package end3r.apielectric.block.entity;

import end3r.apielectric.block.HoneyChargeConduitBlock;
import end3r.apielectric.energy.HoneyChargeProvider;
import end3r.apielectric.energy.HoneyChargeReceiver;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HoneyChargeConduitBlockEntity extends BlockEntity {
    // Transfer properties
    private static final int TRANSFER_RATE = 100; // Amount to transfer per tick
    private static final int TRANSFER_COOLDOWN_MAX = 5; // Ticks between transfers
    private int transferCooldown = 0;
    private boolean wasActive = false;

    // Temporary buffer to store energy while transferring between network elements
    private int energyBuffer = 0;
    private static final int MAX_BUFFER = 1000;

    // Animation properties
    private static final int PROPAGATION_DELAY = 3; // Ticks between conduit activations
    private boolean pendingActivation = false;
    private int activationTimer = 0;
    private BlockPos energySourcePos = null; // The conduit that activated this one
    private Direction energyFlowDirection = null; // Direction that energy is flowing

    public HoneyChargeConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HONEY_CHARGE_CONDUIT_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("TransferCooldown", this.transferCooldown);
        nbt.putInt("EnergyBuffer", this.energyBuffer);
        nbt.putBoolean("PendingActivation", this.pendingActivation);
        nbt.putInt("ActivationTimer", this.activationTimer);

        // Save source position if applicable
        if (this.energySourcePos != null) {
            nbt.putInt("SourcePosX", this.energySourcePos.getX());
            nbt.putInt("SourcePosY", this.energySourcePos.getY());
            nbt.putInt("SourcePosZ", this.energySourcePos.getZ());
        }

        // Save flow direction if applicable
        if (this.energyFlowDirection != null) {
            nbt.putInt("FlowDirection", this.energyFlowDirection.getId());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.transferCooldown = nbt.getInt("TransferCooldown");
        this.energyBuffer = nbt.getInt("EnergyBuffer");
        this.pendingActivation = nbt.getBoolean("PendingActivation");
        this.activationTimer = nbt.getInt("ActivationTimer");

        // Restore source position if saved
        if (nbt.contains("SourcePosX")) {
            this.energySourcePos = new BlockPos(
                    nbt.getInt("SourcePosX"),
                    nbt.getInt("SourcePosY"),
                    nbt.getInt("SourcePosZ")
            );
        }

        // Restore flow direction if saved
        if (nbt.contains("FlowDirection")) {
            this.energyFlowDirection = Direction.byId(nbt.getInt("FlowDirection"));
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, HoneyChargeConduitBlockEntity entity) {
        if (world.isClient()) return;

        // Handle activation propagation
        if (entity.pendingActivation) {
            entity.activationTimer--;
            if (entity.activationTimer <= 0) {
                // Time to activate!
                entity.pendingActivation = false;
                world.setBlockState(pos, state.with(HoneyChargeConduitBlock.ACTIVE, true));

                // Play honey sound when activating
                world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                        SoundCategory.BLOCKS, 0.7F, 0.8F + world.getRandom().nextFloat() * 0.4F);

                // Propagate activation to connected conduits
                entity.propagateActivation(world, pos);

                // Spawn particles
                if (entity.energyBuffer > 0) {
                    entity.spawnEnergyParticles((ServerWorld) world, pos);
                }
            }
            return;
        }

        // Handle deactivation cooldown
        if (entity.transferCooldown > 0) {
            entity.transferCooldown--;

            // If we're active but cooldown is about to expire, check if we should stay active
            if (entity.transferCooldown == 0 && state.get(HoneyChargeConduitBlock.ACTIVE)) {
                boolean shouldStayActive = entity.checkForActiveNeighbors(world, pos);
                if (!shouldStayActive) {
                    world.setBlockState(pos, state.with(HoneyChargeConduitBlock.ACTIVE, false));
                    entity.wasActive = false;
                    entity.energyFlowDirection = null;
                }
            }
            return;
        }

        // Reset cooldown for next tick
        entity.transferCooldown = TRANSFER_COOLDOWN_MAX;

        // Try to transfer energy - only the first conduit in the network will do this
        boolean isTransferring = entity.transferEnergyNetwork(world, pos);

        // If we just started transferring, begin the activation wave
        if (isTransferring && !state.get(HoneyChargeConduitBlock.ACTIVE)) {
            world.setBlockState(pos, state.with(HoneyChargeConduitBlock.ACTIVE, true));
            entity.wasActive = true;

            // Play honey sound when starting to transfer
            world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                    SoundCategory.BLOCKS, 0.7F, 0.8F + world.getRandom().nextFloat() * 0.4F);

            // Begin propagation from energy producer to receivers
            entity.initializeEnergyFlow(world, pos);
            entity.propagateActivation(world, pos);

            // Spawn particles while active
            if (entity.energyBuffer > 0) {
                entity.spawnEnergyParticles((ServerWorld) world, pos);
            }
        }
    }

    /**
     * Determine initial flow direction based on nearby providers and receivers
     */
    private void initializeEnergyFlow(World world, BlockPos pos) {
        // First check for providers in immediate neighbors
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
            BlockState neighborState = world.getBlockState(neighborPos);

            if ((neighborEntity instanceof BaseHoneyChargeBlockEntity &&
                    !(neighborEntity instanceof HoneyChargeFurnaceBlockEntity)) ||
                    (neighborState.getBlock() instanceof HoneyChargeProvider)) {
                // We found a provider, set flow direction away from it
                this.energyFlowDirection = direction.getOpposite();
                return;
            }
        }

        // If no immediate provider, look for receivers
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
            BlockState neighborState = world.getBlockState(neighborPos);

            if ((neighborEntity instanceof HoneyChargeFurnaceBlockEntity) ||
                    (neighborState.getBlock() instanceof HoneyChargeReceiver)) {
                // We found a receiver, set flow direction toward it
                this.energyFlowDirection = direction;
                return;
            }
        }
    }

    /**
     * Propagates activation to neighboring conduits with a delay
     */
    private void propagateActivation(World world, BlockPos sourcePos) {
        for (Direction direction : Direction.values()) {
            // If we have a flow direction, only propagate in that direction
            if (this.energyFlowDirection != null && direction != this.energyFlowDirection) {
                continue;
            }

            BlockPos neighborPos = pos.offset(direction);
            // Don't propagate back to the source that activated us
            if (sourcePos != null && neighborPos.equals(sourcePos)) continue;

            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
            if (neighborEntity instanceof HoneyChargeConduitBlockEntity neighborConduit) {
                BlockState neighborState = world.getBlockState(neighborPos);

                // Only schedule activation if it's not already active or pending activation
                if (!neighborState.get(HoneyChargeConduitBlock.ACTIVE) && !neighborConduit.pendingActivation) {
                    neighborConduit.pendingActivation = true;
                    neighborConduit.activationTimer = PROPAGATION_DELAY;
                    neighborConduit.energySourcePos = this.pos;

                    // Pass along our flow direction
                    if (this.energyFlowDirection != null) {
                        neighborConduit.energyFlowDirection = this.energyFlowDirection;
                    }
                }
            }
        }
    }

    /**
     * Checks if any neighboring conduits are active
     */
    private boolean checkForActiveNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof HoneyChargeConduitBlock &&
                    neighborState.get(HoneyChargeConduitBlock.ACTIVE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Transfers energy between connected blocks in the network
     * @return true if any energy was transferred
     */
    private boolean transferEnergyNetwork(World world, BlockPos pos) {
        // If there's energy in the buffer, try to distribute it first
        if (energyBuffer > 0) {
            return distributeBufferedEnergy(world, pos);
        }

        // Find all providers and extract energy into the buffer
        boolean extracted = extractFromProviders(world, pos);

        // Try to distribute the buffer immediately
        boolean distributed = false;
        if (energyBuffer > 0) {
            distributed = distributeBufferedEnergy(world, pos);
        }

        return extracted || distributed;
    }

    /**
     * Find all energy providers in the network and extract energy
     */
    private boolean extractFromProviders(World world, BlockPos pos) {
        Set<BlockPos> visitedBlocks = new HashSet<>();
        List<BlockPos> providerPositions = new ArrayList<>();

        // Find all providers connected through the conduit network
        findProvidersInNetwork(world, pos, visitedBlocks, providerPositions);

        boolean extracted = false;

        // Extract energy from each provider
        for (BlockPos providerPos : providerPositions) {
            BlockEntity blockEntity = world.getBlockEntity(providerPos);
            if (blockEntity == null) continue;

            if (blockEntity instanceof BaseHoneyChargeBlockEntity honeySrc) {
                int availableEnergy = honeySrc.getStoredHoneyCharge();
                int extractAmount = Math.min(Math.min(availableEnergy, TRANSFER_RATE), MAX_BUFFER - energyBuffer);

                if (extractAmount > 0) {
                    // Consume energy from source
                    int consumed = honeySrc.consumeHoneyCharge(extractAmount);
                    // Add to buffer
                    energyBuffer += consumed;
                    extracted = true;
                }
            } else if (blockEntity.getCachedState().getBlock() instanceof HoneyChargeProvider provider) {
                int providedEnergy = provider.provideHoneyCharge();
                if (providedEnergy > 0) {
                    energyBuffer += Math.min(providedEnergy, MAX_BUFFER - energyBuffer);
                    extracted = true;
                }
            }
        }

        return extracted;
    }

    /**
     * Distribute buffered energy to all receivers in the network
     */
    private boolean distributeBufferedEnergy(World world, BlockPos pos) {
        if (energyBuffer <= 0) return false;

        Set<BlockPos> visitedBlocks = new HashSet<>();
        List<BlockPos> receiverPositions = new ArrayList<>();

        // Find all receivers connected through the conduit network
        findReceiversInNetwork(world, pos, visitedBlocks, receiverPositions);

        if (receiverPositions.isEmpty()) return false;

        // Calculate energy per receiver
        int receiverCount = receiverPositions.size();
        int energyPerReceiver = energyBuffer / receiverCount;
        int remainder = energyBuffer % receiverCount;

        boolean distributed = false;

        // Distribute energy to each receiver
        for (int i = 0; i < receiverPositions.size(); i++) {
            BlockPos receiverPos = receiverPositions.get(i);
            BlockEntity blockEntity = world.getBlockEntity(receiverPos);
            if (blockEntity == null) continue;

            int amountToSend = energyPerReceiver;
            // Add the remainder to the last receiver
            if (i == receiverPositions.size() - 1) {
                amountToSend += remainder;
            }

            if (amountToSend > 0) {
                if (blockEntity instanceof HoneyChargeFurnaceBlockEntity furnace) {
                    furnace.receiveHoneyCharge(amountToSend);
                    energyBuffer -= amountToSend;
                    distributed = true;
                } else if (blockEntity instanceof BaseHoneyChargeBlockEntity honeyDest) {
                    honeyDest.addHoneyCharge(amountToSend);
                    energyBuffer -= amountToSend;
                    distributed = true;
                } else if (blockEntity.getCachedState().getBlock() instanceof HoneyChargeReceiver receiver) {
                    receiver.receiveHoneyCharge(amountToSend);
                    energyBuffer -= amountToSend;
                    distributed = true;
                }
            }
        }

        return distributed;
    }

    /**
     * Recursively finds all providers in the connected network
     */
    private void findProvidersInNetwork(World world, BlockPos centerPos, Set<BlockPos> visitedBlocks, List<BlockPos> providers) {
        if (visitedBlocks.contains(centerPos)) return;
        visitedBlocks.add(centerPos);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = centerPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            // Check if the neighbor is a provider
            if ((neighborEntity instanceof BaseHoneyChargeBlockEntity) ||
                    (neighborState.getBlock() instanceof HoneyChargeProvider)) {
                // Add to list if not already visited
                if (!visitedBlocks.contains(neighborPos)) {
                    providers.add(neighborPos);
                    visitedBlocks.add(neighborPos);
                }
            }
            // If it's another conduit, traverse through it
            else if (neighborEntity instanceof HoneyChargeConduitBlockEntity) {
                findProvidersInNetwork(world, neighborPos, visitedBlocks, providers);
            }
        }
    }

    /**
     * Recursively finds all receivers in the connected network
     */
    private void findReceiversInNetwork(World world, BlockPos centerPos, Set<BlockPos> visitedBlocks, List<BlockPos> receivers) {
        if (visitedBlocks.contains(centerPos)) return;
        visitedBlocks.add(centerPos);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = centerPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            // Check if the neighbor is a receiver
            if ((neighborEntity instanceof HoneyChargeFurnaceBlockEntity) ||
                    (neighborEntity instanceof BaseHoneyChargeBlockEntity && !(neighborEntity instanceof EnergyApiaryBlockEntity)) ||
                    (neighborState.getBlock() instanceof HoneyChargeReceiver)) {
                // Add to list if not already visited and not a provider we already extracted from
                if (!visitedBlocks.contains(neighborPos)) {
                    receivers.add(neighborPos);
                    visitedBlocks.add(neighborPos);
                }
            }
            // If it's another conduit, traverse through it
            else if (neighborEntity instanceof HoneyChargeConduitBlockEntity) {
                findReceiversInNetwork(world, neighborPos, visitedBlocks, receivers);
            }
        }
    }

    /**
     * Spawns energy particles to visualize energy transfer
     */
    private void spawnEnergyParticles(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();

        // Calculate particle count based on energy in buffer
        int particleCount = Math.max(1, Math.min(5, energyBuffer / 200));

        for (int i = 0; i < particleCount; i++) {
            double x, y, z, vx, vy, vz;

            if (energyFlowDirection != null) {
                // Position particles to show directional flow
                // Start position - away from the direction we're flowing
                double offset = 0.3 + (random.nextFloat() * 0.2);
                x = pos.getX() + 0.5 - (energyFlowDirection.getOffsetX() * offset);
                y = pos.getY() + 0.5 - (energyFlowDirection.getOffsetY() * offset);
                z = pos.getZ() + 0.5 - (energyFlowDirection.getOffsetZ() * offset);

                // Velocity - towards the direction we're flowing
                double speed = 0.05 + (random.nextFloat() * 0.05);
                vx = energyFlowDirection.getOffsetX() * speed;
                vy = energyFlowDirection.getOffsetY() * speed;
                vz = energyFlowDirection.getOffsetZ() * speed;

                // Add some randomness to the velocity
                vx += (random.nextFloat() - 0.5) * 0.02;
                vy += (random.nextFloat() - 0.5) * 0.02;
                vz += (random.nextFloat() - 0.5) * 0.02;
            } else {
                // Default non-directional particles
                x = pos.getX() + 0.3 + (random.nextFloat() * 0.4);
                y = pos.getY() + 0.3 + (random.nextFloat() * 0.4);
                z = pos.getZ() + 0.3 + (random.nextFloat() * 0.4);

                // Random velocity
                vx = (random.nextFloat() - 0.5) * 0.05;
                vy = (random.nextFloat() - 0.5) * 0.05;
                vz = (random.nextFloat() - 0.5) * 0.05;
            }

            // Use a mix of particles to create a honey-electric effect
            if (random.nextFloat() < 0.7) {
                world.spawnParticles(ParticleTypes.FALLING_HONEY, x, y, z, 1, vx, vy, vz, 0.01);
            } else {
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, vx, vy, vz, 0.01);
            }
        }
    }
}