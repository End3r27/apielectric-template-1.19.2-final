package end3r.apielectric.bee;

import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity; // Import PathAwareEntity
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyBeeEntity extends PathAwareEntity { // Now extends PathAwareEntity
    private int honeyChargeGenerated = 100; // How much charge this bee generates per tick or interval

    // Constructor fixed
    public EnergyBeeEntity(EntityType<EnergyBeeEntity> type, World world) {
        super(type, world);
    }

    // THIS IS THE IMPORTANT PART - Add this static method to register attributes
    public static DefaultAttributeContainer.Builder createEnergyBeeAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    public void tick() {
        super.tick();
        // Check if the bee is near an Energy Apiary or similar energy-generating block
        if (isNearEnergyApiary()) {
            storeHoneyChargeInApiary();
        }
    }

    // Method to check if the bee is near an EnergyApiaryBlockEntity
    private boolean isNearEnergyApiary() {
        BlockPos beePos = this.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = beePos.add(x, y, z);
                    if (this.world.getBlockEntity(checkPos) instanceof EnergyApiaryBlockEntity) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Store HoneyCharge in the EnergyApiary
    private void storeHoneyChargeInApiary() {
        // Get the actual apiary block entity from the world
        EnergyApiaryBlockEntity apiary = getNearbyEnergyApiary();
        if (apiary != null) {
            apiary.addHoneyCharge(honeyChargeGenerated);
        }
    }

    // Get the nearby apiary block entity
    private EnergyApiaryBlockEntity getNearbyEnergyApiary() {
        BlockPos beePos = this.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = beePos.add(x, y, z);
                    if (this.world.getBlockEntity(checkPos) instanceof EnergyApiaryBlockEntity) {
                        return (EnergyApiaryBlockEntity) this.world.getBlockEntity(checkPos);
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void initGoals() {
        // Remove SwimGoal as it's not necessary for a flying bee
        this.goalSelector.add(1, new WanderAroundGoal(this, 1.0));  // Now works since PathAwareEntity is extended
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));

        // If you want the bee to fly
        this.goalSelector.add(4, new FlyGoal(this, 1.2)); // This will now work
    }
}
