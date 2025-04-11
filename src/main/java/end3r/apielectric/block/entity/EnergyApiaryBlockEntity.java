package end3r.apielectric.block.entity;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class EnergyApiaryBlockEntity extends BaseHoneyChargeBlockEntity {
    public EnergyApiaryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_APIARY_ENTITY, pos, state, 10000);
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("HoneyCharge", this.getStoredHoneyCharge());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setStoredHoneyCharge(nbt.getInt("HoneyCharge"));
    }
    public void receiveEnergy(int amount) {
        this.addHoneyCharge(amount);
        markDirty();

        if (world != null && !world.isClient) {
            // Spawn particles
            ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                    net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, // or CUSTOM one!
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    5,  // count
                    0.2, 0.3, 0.2, // offset
                    0.01  // speed
            );

            // Play a sound
            world.playSound(
                    null, // player (null = everyone nearby hears it)
                    pos,
                    net.minecraft.sound.SoundEvents.ENTITY_BEE_POLLINATE,
                    net.minecraft.sound.SoundCategory.BLOCKS,
                    0.7f, // volume
                    1.2f  // pitch
            );
        }


    }


}
