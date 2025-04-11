package end3r.apielectric.block.entity;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class EnergyApiaryBlockEntity extends BaseHoneyChargeBlockEntity {

    private int transferCooldown = 0;
    private static final int TRANSFER_COOLDOWN_MAX = 20; // Transfer every 20 ticks (1 second)

    public EnergyApiaryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_APIARY_ENTITY, pos, state, 10000);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("HoneyCharge", this.getStoredHoneyCharge());
        nbt.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setStoredHoneyCharge(nbt.getInt("HoneyCharge"));
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    public void receiveEnergy(int amount) {
        this.addHoneyCharge(amount);
        markDirty();

        if (world != null && !world.isClient) {
            // Spawn particles
            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER, // or CUSTOM one!
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    5,  // count
                    0.2, 0.3, 0.2, // offset
                    0.01  // speed
            );

            // Play a sound
            world.playSound(
                    null, // player (null = everyone nearby hears it)
                    pos,
                    SoundEvents.ENTITY_BEE_POLLINATE,
                    SoundCategory.BLOCKS,
                    0.7f, // volume
                    1.2f  // pitch
            );
        }
    }

    /**
     * Transfers honey charge to adjacent blocks that can receive it
     */
    public void transferHoneyChargeToAdjacentBlocks() {
        if (world == null || world.isClient || getStoredHoneyCharge() <= 0) {
            return; // Don't run on client side or if we have no charge
        }

        // The amount of energy to transfer per block
        int transferAmount = 50;

        // Check all adjacent blocks
        BlockPos[] adjacentPositions = new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };

        for (BlockPos adjacentPos : adjacentPositions) {
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            // Check if the adjacent block can receive honey charge
            if (adjacentEntity instanceof HoneyChargeFurnaceBlockEntity furnace) {
                // Calculate how much we can actually transfer
                int chargeToTransfer = Math.min(transferAmount, getStoredHoneyCharge());
                if (chargeToTransfer > 0) {
                    // Transfer the charge
                    furnace.receiveHoneyCharge(chargeToTransfer);
                    // Reduce our stored charge
                    consumeHoneyCharge(chargeToTransfer);

                    // Visual and sound effects
                    ((ServerWorld) world).spawnParticles(
                            ParticleTypes.FALLING_HONEY,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            5, // count
                            0.5, 0.5, 0.5, // offset
                            0.01 // speed
                    );

                    world.playSound(
                            null, // No specific player
                            pos,
                            SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                            SoundCategory.BLOCKS,
                            0.5f, // volume
                            1.0f + world.getRandom().nextFloat() * 0.2f // pitch with variation
                    );

                    markDirty();
                }
            }
        }
    }

    /**
     * Static tick method called by the BlockEntityTicker
     */
    public static void tick(World world, BlockPos pos, BlockState state, EnergyApiaryBlockEntity entity) {
        if (world.isClient()) return;

        // Decrement cooldown if active
        if (entity.transferCooldown > 0) {
            entity.transferCooldown--;
        }

        // When cooldown reaches zero, try to transfer energy
        if (entity.transferCooldown <= 0 && entity.getStoredHoneyCharge() > 0) {
            entity.transferHoneyChargeToAdjacentBlocks();
            // Reset cooldown
            entity.transferCooldown = TRANSFER_COOLDOWN_MAX;
        }
    }
}