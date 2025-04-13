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


public class CombCapacitorBlockEntity extends BaseHoneyChargeBlockEntity implements HoneyChargeReceiver {

    public static final int MAX_HONEYCHARGE = 80000;
    private int transferCooldown = 0;
    private static final int TRANSFER_COOLDOWN_MAX = 20; // Transfer every 20 ticks (1 second)

    // Visual effect tracking
    private boolean receivedEnergy = false;
    private int energyReceivedThisTick = 0;

    public CombCapacitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMB_CAPACITOR_ENTITY, pos, state, MAX_HONEYCHARGE);
    }

    @Override
    public int receiveHoneyCharge(int amount) {
        if (amount <= 0) return 0;

        int spaceAvailable = getMaxCharge() - getStoredHoneyCharge();
        int acceptedAmount = Math.min(amount, spaceAvailable);

        if (acceptedAmount > 0) {
            super.addHoneyCharge(acceptedAmount);
            this.energyReceivedThisTick += acceptedAmount;
            this.receivedEnergy = true;
            markDirty();
        }

        return acceptedAmount;
    }

    // This method is not overriding but instead calling the parent method
    // and adding additional functionality
    public void trackEnergyReceived(int amount) {
        if (amount > 0) {
            this.energyReceivedThisTick += amount;
            this.receivedEnergy = true;
        }
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
     * Displays visual effects for receiving honey charge
     */
    private void showEnergyReceivedEffects() {
        if (world != null && !world.isClient && receivedEnergy && energyReceivedThisTick > 0) {
            // Adjust particle count based on amount received
            int particleCount = Math.min(5, 1 + (energyReceivedThisTick / 200));

            world.playSound(
                    null,
                    pos,
                    SoundEvents.BLOCK_HONEY_BLOCK_PLACE,
                    SoundCategory.BLOCKS,
                    0.6f,
                    1.1f + world.getRandom().nextFloat() * 0.2f
            );

            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.FALLING_HONEY,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    particleCount, // number of particles based on energy received
                    0.3, 0.2, 0.3, // spread
                    0.01 // speed
            );

            // Reset flags for next tick
            receivedEnergy = false;
            energyReceivedThisTick = 0;
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
        boolean didTransfer = false;

        // Check all adjacent blocks
        BlockPos[] adjacentPositions = new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };

        for (BlockPos adjacentPos : adjacentPositions) {
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            // Skip if we're out of energy
            if (getStoredHoneyCharge() <= 0) break;

            // IMPORTANT: Skip if the adjacent entity is an EnergyApiary
            // This prevents capacitors from transferring energy to apiaries
            if (adjacentEntity instanceof EnergyApiaryBlockEntity) {
                continue;
            }

            // Check for all types of receivers
            if (adjacentEntity != null) {
                int chargeToTransfer = Math.min(transferAmount, getStoredHoneyCharge());
                if (chargeToTransfer > 0) {
                    int amountAccepted = 0;

                    if (adjacentEntity instanceof HoneyChargeFurnaceBlockEntity furnace) {
                        // Transfer to furnace
                        furnace.receiveHoneyCharge(chargeToTransfer);
                        amountAccepted = chargeToTransfer; // Assume all was accepted
                    } else if (adjacentEntity instanceof HoneyChargeReceiver receiver &&
                            !(adjacentEntity instanceof EnergyApiaryBlockEntity)) {
                        // Transfer to any HoneyChargeReceiver except EnergyApiary
                        amountAccepted = receiver.receiveHoneyCharge(chargeToTransfer);
                    } else if (adjacentEntity.getCachedState().getBlock() instanceof HoneyChargeReceiver blockReceiver) {
                        // Transfer to blocks implementing HoneyChargeReceiver
                        amountAccepted = blockReceiver.receiveHoneyCharge(chargeToTransfer);
                    }

                    if (amountAccepted > 0) {
                        consumeHoneyCharge(amountAccepted);
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
                    3, // count
                    0.5, 0.5, 0.5, // offset
                    0.01 // speed
            );

            world.playSound(
                    null, // No specific player
                    pos,
                    SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                    SoundCategory.BLOCKS,
                    0.4f, // volume
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

        // Show visual effects if we received energy
        entity.showEnergyReceivedEffects();

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