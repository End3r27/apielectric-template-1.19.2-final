package end3r.apielectric.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;

public class EnergyBeeEntity extends BeeEntity {

    private int storedEnergy = 0;
    private static final int MAX_ENERGY = 1000;

    public EnergyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    // Override to handle custom data
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

    // Getter and Setter for Energy
    public int getStoredEnergy() {
        return storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.min(energy, MAX_ENERGY);
    }

    public void addEnergy(int amount) {
        setStoredEnergy(this.storedEnergy + amount);
    }

    public int getMaxStoredEnergy() {
        return MAX_ENERGY;
    }


    // Set entity attributes (Health, Speed)
    public static DefaultAttributeContainer.Builder createEnergyBeeAttributes() {
        return BeeEntity.createBeeAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0) // Health
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25); // Movement speed
    }

    // Initialize goals, including the energy charging goal
    @Override
    protected void initGoals() {
        super.initGoals();
        // Add custom goal for charging from flowers or other behavior
        this.goalSelector.add(5, new EnergyBeeChargeFromFlowerGoal(this));
        this.goalSelector.add(6, new end3r.apielectric.bee.GoToApiaryGoal(this));


        }

}


