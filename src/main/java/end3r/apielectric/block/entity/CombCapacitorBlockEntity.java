package end3r.apielectric.block.entity;

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
import net.minecraft.world.World;


public class CombCapacitorBlockEntity extends BaseHoneyChargeBlockEntity {

    public static final int MAX_HONEYCHARGE = 80000;
    private int transferCooldown = 0; // Remove static keyword
    private static final int TRANSFER_COOLDOWN_MAX = 20; // Transfer every 20 ticks (1 second)


    public CombCapacitorBlockEntity(net.minecraft.util.math.BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMB_CAPACITOR_ENTITY, pos, state, MAX_HONEYCHARGE);
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

    /**
     * Transfers honey charge to adjacent blocks that can receive it
     */
    public void transferHoneyChargeToAdjacentBlocks() {
        if (world == null || world.isClient || getStoredHoneyCharge() <= 0) {
            return; // Don't run on client side or if we have no charge
        }

        // The amount of energy to transfer per block
        int transferAmount = 50;
        boolean didTransfer = false;

        // Check all adjacent blocks
        BlockPos[] adjacentPositions = new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };

        for (BlockPos adjacentPos : adjacentPositions) {
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            // Check for all types of receivers
            if (adjacentEntity != null) {
                int chargeToTransfer = Math.min(transferAmount, getStoredHoneyCharge());
                if (chargeToTransfer > 0) {
                    if (adjacentEntity instanceof HoneyChargeFurnaceBlockEntity furnace) {
                        // Transfer to furnace
                        furnace.receiveHoneyCharge(chargeToTransfer);
                        consumeHoneyCharge(chargeToTransfer);
                        didTransfer = true;
                    } else if (adjacentEntity instanceof HoneyChargeReceiver receiver) {
                        // Transfer to any HoneyChargeReceiver
                        receiver.receiveHoneyCharge(chargeToTransfer);
                        consumeHoneyCharge(chargeToTransfer);
                        didTransfer = true;
                    } else if (adjacentEntity.getCachedState().getBlock() instanceof HoneyChargeReceiver blockReceiver) {
                        // Transfer to blocks implementing HoneyChargeReceiver
                        blockReceiver.receiveHoneyCharge(chargeToTransfer);
                        consumeHoneyCharge(chargeToTransfer);
                        didTransfer = true;
                    }
                }
            }
        }

        if (didTransfer) {
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

    /**
     * Static tick method called by the BlockEntityTicker
     */
    public static void tick(World world, BlockPos pos, BlockState state, CombCapacitorBlockEntity entity) {
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