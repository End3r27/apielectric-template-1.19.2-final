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

    public HoneyChargeConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HONEY_CHARGE_CONDUIT_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("TransferCooldown", this.transferCooldown);
        nbt.putInt("EnergyBuffer", this.energyBuffer);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.transferCooldown = nbt.getInt("TransferCooldown");
        this.energyBuffer = nbt.getInt("EnergyBuffer");
    }

    public static void tick(World world, BlockPos pos, BlockState state, HoneyChargeConduitBlockEntity entity) {
        if (world.isClient()) return;

        // Decrement cooldown
        if (entity.transferCooldown > 0) {
            entity.transferCooldown--;
            return;
        }

        // Reset cooldown for next tick
        entity.transferCooldown = TRANSFER_COOLDOWN_MAX;

        // Check if the conduit is part of a valid network (has both providers and receivers)
        Set<BlockPos> visitedBlocks = new HashSet<>();
        List<BlockPos> providers = new ArrayList<>();
        List<BlockPos> receivers = new ArrayList<>();

        entity.findEnergyComponentsInNetwork(world, pos, visitedBlocks, providers, receivers);

        // Only allow the conduit to work if connecting at least one provider and one receiver
        boolean isValidNetwork = !providers.isEmpty() && !receivers.isEmpty();

        // Try to transfer energy only if the network is valid
        boolean isTransferring = isValidNetwork && entity.transferEnergyNetwork(world, pos, providers, receivers);

        // Update block state if transfer status changed
        boolean currentlyActive = state.get(HoneyChargeConduitBlock.ACTIVE);
        if (isTransferring != currentlyActive) {
            // Update all conduits in the network
            if (isTransferring) {
                entity.activateAllConduits(world, pos);
            } else {
                world.setBlockState(pos, state.with(HoneyChargeConduitBlock.ACTIVE, false));
            }

            // Play honey sound when starting to transfer
            if (isTransferring && !entity.wasActive) {
                world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                        SoundCategory.BLOCKS, 0.7F, 0.8F + world.getRandom().nextFloat() * 0.4F);
            }
            entity.wasActive = isTransferring;
        }
        // Deactivate if not a valid network
        else if (!isValidNetwork && currentlyActive) {
            entity.deactivateAllConduits(world, pos);
            entity.wasActive = false;
        }

        // Add particles while active
        if (isTransferring && entity.energyBuffer > 0) {
            entity.spawnEnergyParticles((ServerWorld) world, pos);
        }
    }

    /**
     * Activates all conduits in the network
     */
    private void activateAllConduits(World world, BlockPos pos) {
        Set<BlockPos> visitedBlocks = new HashSet<>();
        activateConnectedConduits(world, pos, visitedBlocks);
    }

    /**
     * Deactivates all conduits in the network
     */
    private void deactivateAllConduits(World world, BlockPos pos) {
        Set<BlockPos> visitedBlocks = new HashSet<>();
        deactivateConnectedConduits(world, pos, visitedBlocks);
    }

    /**
     * Recursively activates all connected conduits
     */
    private void activateConnectedConduits(World world, BlockPos centerPos, Set<BlockPos> visitedBlocks) {
        if (visitedBlocks.contains(centerPos)) return;
        visitedBlocks.add(centerPos);

        // Activate this conduit
        BlockState state = world.getBlockState(centerPos);
        if (state.getBlock() instanceof HoneyChargeConduitBlock && !state.get(HoneyChargeConduitBlock.ACTIVE)) {
            world.setBlockState(centerPos, state.with(HoneyChargeConduitBlock.ACTIVE, true));
        }

        // Look for connected conduits
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = centerPos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            // If it's another conduit, activate it too
            if (neighborEntity instanceof HoneyChargeConduitBlockEntity) {
                activateConnectedConduits(world, neighborPos, visitedBlocks);
            }
        }
    }

    /**
     * Recursively deactivates all connected conduits
     */
    private void deactivateConnectedConduits(World world, BlockPos centerPos, Set<BlockPos> visitedBlocks) {
        if (visitedBlocks.contains(centerPos)) return;
        visitedBlocks.add(centerPos);

        // Deactivate this conduit
        BlockState state = world.getBlockState(centerPos);
        if (state.getBlock() instanceof HoneyChargeConduitBlock && state.get(HoneyChargeConduitBlock.ACTIVE)) {
            world.setBlockState(centerPos, state.with(HoneyChargeConduitBlock.ACTIVE, false));
        }

        // Look for connected conduits
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = centerPos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            // If it's another conduit, deactivate it too
            if (neighborEntity instanceof HoneyChargeConduitBlockEntity) {
                deactivateConnectedConduits(world, neighborPos, visitedBlocks);
            }
        }
    }

    /**
     * Transfers energy between connected blocks in the network
     * @return true if any energy was transferred
     */
    private boolean transferEnergyNetwork(World world, BlockPos pos, List<BlockPos> providers, List<BlockPos> receivers) {
        // If there's energy in the buffer, try to distribute it first
        if (energyBuffer > 0) {
            return distributeBufferedEnergy(world, receivers);
        }

        // Extract energy from providers into the buffer
        boolean extracted = extractFromProviders(world, providers);

        // Try to distribute the buffer immediately
        boolean distributed = false;
        if (energyBuffer > 0) {
            distributed = distributeBufferedEnergy(world, receivers);
        }

        return extracted || distributed;
    }

    /**
     * Find all energy providers in the network and extract energy
     */
    private boolean extractFromProviders(World world, List<BlockPos> providerPositions) {
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
    private boolean distributeBufferedEnergy(World world, List<BlockPos> receiverPositions) {
        if (energyBuffer <= 0) return false;

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
     * Finds all energy components (providers and receivers) in the connected network
     */
    private void findEnergyComponentsInNetwork(World world, BlockPos centerPos, Set<BlockPos> visitedBlocks,
                                               List<BlockPos> providers, List<BlockPos> receivers) {
        if (visitedBlocks.contains(centerPos)) return;
        visitedBlocks.add(centerPos);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = centerPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            // Skip if already visited
            if (visitedBlocks.contains(neighborPos)) continue;

            // Check if the neighbor is a provider
            if ((neighborEntity instanceof BaseHoneyChargeBlockEntity && isEnergyProvider(neighborEntity)) ||
                    (neighborState.getBlock() instanceof HoneyChargeProvider)) {
                providers.add(neighborPos);
                visitedBlocks.add(neighborPos);
            }
            // Check if the neighbor is a receiver
            else if ((neighborEntity instanceof HoneyChargeFurnaceBlockEntity) ||
                    (neighborEntity instanceof BaseHoneyChargeBlockEntity && isEnergyReceiver(neighborEntity)) ||
                    (neighborState.getBlock() instanceof HoneyChargeReceiver)) {
                receivers.add(neighborPos);
                visitedBlocks.add(neighborPos);
            }
            // If it's another conduit, traverse through it
            else if (neighborEntity instanceof HoneyChargeConduitBlockEntity) {
                findEnergyComponentsInNetwork(world, neighborPos, visitedBlocks, providers, receivers);
            }
        }
    }

    /**
     * Checks if the block entity is an energy provider
     */
    private boolean isEnergyProvider(BlockEntity entity) {
        // EnergyApiary seems to be a provider based on previous code
        return entity instanceof EnergyApiaryBlockEntity;
    }

    /**
     * Checks if the block entity is an energy receiver
     */
    private boolean isEnergyReceiver(BlockEntity entity) {
        // BaseHoneyChargeBlockEntity that is not an EnergyApiaryBlockEntity
        return entity instanceof BaseHoneyChargeBlockEntity && !(entity instanceof EnergyApiaryBlockEntity);
    }

    /**
     * Spawns energy particles to visualize energy transfer
     */
    private void spawnEnergyParticles(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();

        // Calculate particle count based on energy in buffer
        int particleCount = Math.max(1, Math.min(5, energyBuffer / 200));

        for (int i = 0; i < particleCount; i++) {
            // Position particles within the block with some randomness
            double x = pos.getX() + 0.3 + (random.nextFloat() * 0.4);
            double y = pos.getY() + 0.3 + (random.nextFloat() * 0.4);
            double z = pos.getZ() + 0.3 + (random.nextFloat() * 0.4);

            // Determine particle velocity (slight movement in random directions)
            double vx = (random.nextFloat() - 0.5) * 0.05;
            double vy = (random.nextFloat() - 0.5) * 0.05;
            double vz = (random.nextFloat() - 0.5) * 0.05;

            // Use a mix of particles to create a honey-electric effect
            if (random.nextFloat() < 0.7) {
                world.spawnParticles(ParticleTypes.FALLING_HONEY, x, y, z, 1, vx, vy, vz, 0.01);
            } else {
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, vx, vy, vz, 0.01);
            }
        }
    }
}