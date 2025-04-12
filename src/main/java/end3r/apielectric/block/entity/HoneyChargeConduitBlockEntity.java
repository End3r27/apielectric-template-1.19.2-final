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

        // Check if the conduit is part of a valid network
        Set<BlockPos> visitedBlocks = new HashSet<>();
        List<BlockPos> providers = new ArrayList<>();
        List<BlockPos> receivers = new ArrayList<>();
        List<BlockPos> storageBlocks = new ArrayList<>(); // New list for storage blocks

        entity.findEnergyComponentsInNetwork(world, pos, visitedBlocks, providers, receivers, storageBlocks);

        // Network is valid if there's at least one provider and one receiver
        // OR if there are storage blocks with energy that can feed receivers
        boolean hasValidProviders = !providers.isEmpty();
        boolean hasValidStorageWithEnergy = entity.checkStorageBlocksForEnergy(world, storageBlocks);
        boolean isValidNetwork = (hasValidProviders || hasValidStorageWithEnergy) && !receivers.isEmpty();

        // Try to transfer energy only if the network is valid
        boolean isTransferring = false;
        if (isValidNetwork) {
            // First, try to move energy from storage blocks if needed
            if (entity.energyBuffer < TRANSFER_RATE && !storageBlocks.isEmpty()) {
                entity.extractFromStorageBlocks(world, storageBlocks);
            }

            // Then try the normal transfer process
            isTransferring = entity.transferEnergyNetwork(world, pos, providers, receivers, storageBlocks);
        }

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
     * Checks if any storage blocks in the network have energy
     */
    private boolean checkStorageBlocksForEnergy(World world, List<BlockPos> storageBlocks) {
        for (BlockPos storagePos : storageBlocks) {
            BlockEntity blockEntity = world.getBlockEntity(storagePos);
            if (blockEntity instanceof BaseHoneyChargeBlockEntity storage) {
                if (storage.getStoredHoneyCharge() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extracts energy from storage blocks into the buffer
     */
    private boolean extractFromStorageBlocks(World world, List<BlockPos> storageBlocks) {
        boolean extracted = false;
        int spaceInBuffer = MAX_BUFFER - energyBuffer;

        if (spaceInBuffer <= 0) return false;

        for (BlockPos storagePos : storageBlocks) {
            BlockEntity blockEntity = world.getBlockEntity(storagePos);
            if (!(blockEntity instanceof BaseHoneyChargeBlockEntity storage)) continue;

            int availableEnergy = storage.getStoredHoneyCharge();
            int extractAmount = Math.min(Math.min(availableEnergy, TRANSFER_RATE), spaceInBuffer);

            if (extractAmount > 0) {
                int consumed = storage.consumeHoneyCharge(extractAmount);
                energyBuffer += consumed;
                extracted = true;

                // Update remaining space in buffer
                spaceInBuffer -= consumed;
                if (spaceInBuffer <= 0) break;
            }
        }

        return extracted;
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
    private boolean transferEnergyNetwork(World world, BlockPos pos, List<BlockPos> providers,
                                          List<BlockPos> receivers, List<BlockPos> storageBlocks) {
        // Try to distribute buffered energy first
        if (energyBuffer > 0) {
            // First try filling storage blocks that aren't full
            boolean storedSome = distributeToStorageBlocks(world, storageBlocks);

            // Then send to receivers
            boolean sentSome = distributeBufferedEnergy(world, receivers);

            return storedSome || sentSome;
        }

        // Extract energy from providers into the buffer
        boolean extracted = extractFromProviders(world, providers);

        // Try to distribute the buffer immediately
        boolean distributed = false;
        if (energyBuffer > 0) {
            boolean storedSome = distributeToStorageBlocks(world, storageBlocks);
            boolean sentSome = distributeBufferedEnergy(world, receivers);
            distributed = storedSome || sentSome;
        }

        return extracted || distributed;
    }

    /**
     * Distributes energy to storage blocks that aren't full
     */
    private boolean distributeToStorageBlocks(World world, List<BlockPos> storageBlocks) {
        if (energyBuffer <= 0 || storageBlocks.isEmpty()) return false;

        // Only distribute a portion of energy to storage (prioritize receivers)
        int energyForStorage = Math.min(energyBuffer / 2, TRANSFER_RATE);
        if (energyForStorage <= 0) return false;

        boolean distributed = false;

        // Find non-full storage blocks
        List<BlockPos> availableStorage = new ArrayList<>();
        for (BlockPos storagePos : storageBlocks) {
            BlockEntity blockEntity = world.getBlockEntity(storagePos);
            if (blockEntity instanceof BaseHoneyChargeBlockEntity storage) {
                if (storage.getStoredHoneyCharge() < storage.getMaxCharge()) {
                    availableStorage.add(storagePos);
                }
            }
        }

        if (availableStorage.isEmpty()) return false;

        // Distribute energy evenly
        int storageCount = availableStorage.size();
        int energyPerStorage = energyForStorage / storageCount;
        int remainder = energyForStorage % storageCount;

        for (int i = 0; i < availableStorage.size(); i++) {
            BlockPos storagePos = availableStorage.get(i);
            BlockEntity blockEntity = world.getBlockEntity(storagePos);

            if (blockEntity instanceof BaseHoneyChargeBlockEntity storage) {
                int amountToSend = energyPerStorage;
                if (i == availableStorage.size() - 1) {
                    amountToSend += remainder;
                }

                if (amountToSend > 0) {
                    storage.addHoneyCharge(amountToSend);
                    energyBuffer -= amountToSend;
                    distributed = true;
                }
            }
        }

        return distributed;
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
     * Finds all energy components (providers, receivers, and storage blocks) in the connected network
     */
    private void findEnergyComponentsInNetwork(World world, BlockPos centerPos, Set<BlockPos> visitedBlocks,
                                               List<BlockPos> providers, List<BlockPos> receivers,
                                               List<BlockPos> storageBlocks) {
        if (visitedBlocks.contains(centerPos)) return;
        visitedBlocks.add(centerPos);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = centerPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            // Skip if already visited
            if (visitedBlocks.contains(neighborPos)) continue;

            // Check what type of energy component the neighbor is
            if (neighborEntity instanceof BaseHoneyChargeBlockEntity honeyBlock) {
                if (neighborEntity instanceof CombCapacitorBlockEntity) {
                    // CombCapacitor is treated as a storage block
                    storageBlocks.add(neighborPos);
                } else if (isEnergyProvider(neighborEntity)) {
                    providers.add(neighborPos);
                } else if (isEnergyReceiver(neighborEntity)) {
                    receivers.add(neighborPos);
                } else {
                    // If it's neither a dedicated provider nor receiver, it's a storage block
                    storageBlocks.add(neighborPos);
                }
                visitedBlocks.add(neighborPos);
            }
            // Check for block-based providers and receivers
            else if (neighborState.getBlock() instanceof HoneyChargeProvider) {
                providers.add(neighborPos);
                visitedBlocks.add(neighborPos);
            } else if (neighborEntity instanceof HoneyChargeFurnaceBlockEntity) {
                receivers.add(neighborPos);
                visitedBlocks.add(neighborPos);
            } else if (neighborState.getBlock() instanceof HoneyChargeReceiver) {
                receivers.add(neighborPos);
                visitedBlocks.add(neighborPos);
            }
            // If it's another conduit, traverse through it
            else if (neighborEntity instanceof HoneyChargeConduitBlockEntity) {
                findEnergyComponentsInNetwork(world, neighborPos, visitedBlocks, providers, receivers, storageBlocks);
            }
        }
    }

    /**
     * Checks if the block entity is an energy provider
     */
    private boolean isEnergyProvider(BlockEntity entity) {
        // EnergyApiary is a dedicated provider
        return entity instanceof EnergyApiaryBlockEntity;
    }

    /**
     * Checks if the block entity is an energy receiver
     */
    private boolean isEnergyReceiver(BlockEntity entity) {
        // CombCapacitor is no longer considered a dedicated receiver
        // It will be handled as a storage block that can both receive and provide energy
        return entity instanceof HoneyChargeFurnaceBlockEntity;
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