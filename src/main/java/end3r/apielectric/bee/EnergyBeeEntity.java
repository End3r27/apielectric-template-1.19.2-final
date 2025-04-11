package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.world.ServerWorld;

public class EnergyBeeEntity extends PathAwareEntity implements Flutterer {
    private static final float BEE_PITCH_MULTIPLIER = 0.8F;
    private float rollAmount = 0.0F;
    private float rollAmountO = 0.0F;
    private int timeSinceSting;
    private int timeInAir; // Track time in air

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
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6) // Flying speed
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3); // Ground movement speed
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
    }

    @Override
    public void tick() {
        super.tick();
        this.updateRoll();

        if (!this.hasNoGravity() && !this.isOnGround()) {
            this.timeInAir++;
        } else {
            this.timeInAir = 0;
        }

        // Apply upward movement when flying
        if (!this.onGround && this.getVelocity().y < 0.0) {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
    }

    private void updateRoll() {
        this.rollAmountO = this.rollAmount;
        if (this.isFlying()) {
            this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
        } else {
            this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
        }
    }

    // Helper method to check if the bee is flying
    public boolean isFlying() {
        return !this.onGround;
    }

    @Override
    public void travel(Vec3d movementInput) {
        // If the bee is a flier and not on the ground, use flying movement
        if (this.isLogicalSideForUpdatingMovement() && !this.onGround) {
            this.updateVelocity(0.1F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());

            // Apply some drag/friction
            this.setVelocity(this.getVelocity().multiply(0.91f));

            // Apply slight upward momentum to counteract gravity
            if (!this.hasNoGravity() && this.timeInAir > 10) {
                Vec3d vec3d = this.getVelocity();
                this.setVelocity(vec3d.x, Math.max(vec3d.y, -0.05), vec3d.z);
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public boolean isInAir() {
        return !this.onGround;
    }

    // Getter for the roll amount for animations
    public float getRollAmount(float tickDelta) {
        return this.rollAmountO + (this.rollAmount - this.rollAmountO) * tickDelta;
    }
}

