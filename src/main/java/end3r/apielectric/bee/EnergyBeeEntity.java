package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;

public class EnergyBeeEntity extends BeeEntity {

    private int storedEnergy = 0;
    private static final int MAX_ENERGY = 100;

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

    // In EnergyBeeEntity.java
    public void addEnergy(int amount) {
        int oldEnergy = this.storedEnergy;
        this.storedEnergy = Math.min(this.storedEnergy + amount, MAX_ENERGY);

        if (oldEnergy != this.storedEnergy) {
            ApiElectric.LOGGER.info("Energy Bee energy changed: " + oldEnergy + " -> " + this.storedEnergy);
        }
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

        // Add this debug message
        ApiElectric.LOGGER.info("Initializing goals for Energy Bee");

        // Make the charging goal higher priority (lower number)
        this.goalSelector.add(2, new EnergyBeeChargeFromFlowerGoal(this));
        this.goalSelector.add(3, new GoToApiaryGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Log only occasionally to avoid spam
        if (this.getWorld().getTime() % 200 == 0) {  // Every 10 seconds
            ApiElectric.LOGGER.info("Energy Bee ticking: ID=" + this.getId() + ", Energy=" + this.getStoredEnergy());
        }
    }

}


