package end3r.apielectric.block.entity;

import end3r.apielectric.energy.HoneyChargeProvider;
import end3r.apielectric.energy.HoneyChargeReceiver;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import end3r.apielectric.block.HoneyChargeConduitBlock;

public class HoneyChargeConduitBlockEntity extends BlockEntity {
    private boolean wasActive = false;
    private int transferCooldown = 0;

    public HoneyChargeConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HONEY_CHARGE_CONDUIT_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HoneyChargeConduitBlockEntity entity) {
        boolean isTransferring = entity.transferEnergy(world, pos);

        // Update block state if transfer status changed
        boolean currentlyActive = state.get(HoneyChargeConduitBlock.ACTIVE);
        if (isTransferring != currentlyActive) {
            world.setBlockState(pos, state.with(HoneyChargeConduitBlock.ACTIVE, isTransferring));

            // Play honey dripping sound when starting to transfer
            if (isTransferring && !entity.wasActive) {
                world.playSound(
                        null, // No specific player
                        pos,
                        SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, // Or another appropriate honey sound
                        SoundCategory.BLOCKS,
                        1.0F, // Volume
                        0.8F + world.random.nextFloat() * 0.4F // Pitch with slight variation
                );
            }

            entity.wasActive = isTransferring;
        }

        // Add particles continuously while active
        if (isTransferring && world.isClient()) {
            entity.addHoneyParticles(world, pos);
        }
    }

    private void addHoneyParticles(World world, BlockPos pos) {
        Random random = world.getRandom();

        // Add dripping honey particles
        if (random.nextFloat() < 0.4F) { // Adjust probability to control particle density
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
            double y = pos.getY() + 0.2D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;

            // Drip honey particles downward
            world.addParticle(
                    ParticleTypes.DRIPPING_HONEY, // Honey drip particle
                    x, y, z,
                    0.0D, // No X velocity
                    -0.05D, // Slow downward velocity
                    0.0D  // No Z velocity
            );
        }

        // Add some happy villager particles for energy transfer visualization
        if (random.nextFloat() < 0.2F) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
            double y = pos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;

            world.addParticle(
                    ParticleTypes.FALLING_HONEY,
                    x, y, z,
                    (random.nextDouble() - 0.5D) * 0.1D,
                    (random.nextDouble() - 0.5D) * 0.1D,
                    (random.nextDouble() - 0.5D) * 0.1D
            );
        }
    }

    public boolean transferEnergy(World world, BlockPos pos) {
        boolean didTransfer = false;
        BlockPos[] neighbors = new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };

        for (BlockPos neighbor : neighbors) {
            BlockState neighborState = world.getBlockState(neighbor);
            if (neighborState.getBlock() instanceof HoneyChargeProvider provider) {
                int energyToTransfer = provider.provideHoneyCharge();
                if (energyToTransfer > 0) {
                    for (BlockPos adjNeighbor : neighbors) {
                        if (adjNeighbor.equals(neighbor)) continue; // Skip the provider

                        BlockState adjNeighborState = world.getBlockState(adjNeighbor);
                        if (adjNeighborState.getBlock() instanceof HoneyChargeReceiver receiver) {
                            receiver.receiveHoneyCharge(energyToTransfer);
                            didTransfer = true;
                        }
                    }
                }
            }
        }

        return didTransfer;
    }
}