package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;

import java.lang.reflect.Field;

public class EnergyBeeEntity extends PathAwareEntity implements Flutterer {
    public EnergyBeeEntity(EntityType<? extends EnergyBeeEntity> entityType, World world) {
        super(entityType, world);
    }

    private int storedEnergy = 0;
    private static final int MAX_ENERGY = 100;


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
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4D));
        // CUSTOM GOALS - Higher priority than vanilla behaviors
        this.goalSelector.add(3, new EnergyBeeChargeFromFlowerGoal(this));
        this.goalSelector.add(4, new GoToApiaryGoal(this));

        // Other vanilla goals that don't interfere with flower behavior
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        // Notably NOT including:
        // - BeeEntity.PollinateGoal
        // - BeeEntity.GrowCropsGoal
        // - BeeEntity.FindFlowerGoal
        // - BeeEntity.FindHiveGoal
    }

    @Override
    public boolean isInAir() {
        return true;
    }
}


