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

        // Try to transfer energy
        boolean isTransferring = entity.transferEnergyNetwork(world, pos);

        // Update block state if transfer status changed
        boolean currentlyActive = state.get(HoneyChargeConduitBlock.ACTIVE);
        if (isTransferring != currentlyActive) {
            world.setBlockState(pos, state.with(HoneyChargeConduitBlock.ACTIVE, isTransferring));

            // Play honey sound when starting to transfer
            if (isTransferring && !entity.wasActive) {
                world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                        SoundCategory.BLOCKS, 0.7F, 0.8F + world.getRandom().nextFloat() * 0.4F);
            }
            entity.wasActive = isTransferring;
        }

        // Add particles while active
        if (isTransferring && entity.energyBuffer > 0) {
            entity.spawnEnergyParticles((ServerWorld) world, pos);
        }
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