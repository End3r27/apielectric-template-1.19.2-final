package end3r.apielectric.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class EnergyBeeEntity extends BeeEntity {

    private int storedEnergy = 0;
    private static final int MAX_ENERGY = 1000;

    public EnergyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("StoredEnergy", storedEnergy);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        storedEnergy = nbt.getInt("StoredEnergy");
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.min(energy, MAX_ENERGY);
    }

    public void addEnergy(int amount) {
        setStoredEnergy(this.storedEnergy + amount);
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }
    @Override
    protected void initGoals() {
        super.initGoals();

        // Example: prioritize nearby flowers
        this.goalSelector.add(5, new EnergyBeeChargeFromFlowerGoal(this));

        // You can replace vanilla goals later if needed
    }

}