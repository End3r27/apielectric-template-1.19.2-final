// Replace or modify your EnergyBeeChargeFromFlowerGoal.java
package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockTags;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class EnergyBeeChargeFromFlowerGoal extends Goal {
    private final EnergyBeeEntity bee;
    private int cooldown = 0;

    public EnergyBeeChargeFromFlowerGoal(EnergyBeeEntity bee) {
        this.bee = bee;
        // Use multiple controls to properly handle the goal
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // Only try to check for flowers every so often
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        cooldown = 20; // Check every second

        // Simple log to see if this goal is being evaluated
        ApiElectric.LOGGER.info("Energy Bee checking if can charge from flower");

        // Check if there's a flower below the bee
        return isFlowerBelow();
    }

    private boolean isFlowerBelow() {
        World world = bee.getWorld();
        BlockPos beePos = bee.getBlockPos();

        // Check blocks in a small radius around the bee, not just below
        for (int y = -1; y <= 1; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = beePos.add(x, y, z);
                    if (world.getBlockState(checkPos).isIn(ModBlockTags.ENERGIZED_FLOWERS)) {
                        ApiElectric.LOGGER.info("Energy Bee found flower at " + checkPos);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void start() {
        ApiElectric.LOGGER.info("Energy Bee starting to charge from flower");
    }

    @Override
    public void tick() {
        // Find the nearest flower
        BlockPos nearestFlower = findNearestFlower();

        if (nearestFlower != null) {
            // Move toward the flower if not already there
            double distance = bee.getBlockPos().getSquaredDistance(nearestFlower);
            if (distance > 1.5) {
                bee.getNavigation().startMovingTo(
                        nearestFlower.getX() + 0.5,
                        nearestFlower.getY() + 0.5,
                        nearestFlower.getZ() + 0.5,
                        0.5D
                );
            } else {
                // We're at the flower, stop moving and start charging
                bee.getNavigation().stop();

                // Simple charging
                int oldEnergy = bee.getStoredEnergy();
                bee.addEnergy(5);

                // Particles and effects
                if (bee.getWorld().isClient() && oldEnergy != bee.getStoredEnergy()) {
                    bee.getWorld().addParticle(
                            ParticleTypes.ELECTRIC_SPARK,
                            bee.getX(), bee.getY() + 0.5, bee.getZ(),
                            0, 0.1, 0
                    );
                }
            }
        }
    }

    private BlockPos findNearestFlower() {
        World world = bee.getWorld();
        BlockPos beePos = bee.getBlockPos();
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        // Check in a radius around bee
        int radius = 5;
        for (int y = -2; y <= 2; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = beePos.add(x, y, z);
                    if (world.getBlockState(checkPos).isIn(ModBlockTags.ENERGIZED_FLOWERS)) {
                        double dist = checkPos.getSquaredDistance(beePos);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = checkPos;
                        }
                    }
                }
            }
        }

        return nearest;
    }

    @Override
    public boolean shouldContinue() {
        return isFlowerBelow();
    }

    @Override
    public void stop() {
        ApiElectric.LOGGER.info("Energy Bee stopped charging");
    }
}