package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockTags;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class EnergyBeeChargeFromFlowerGoal extends Goal {
    private final EnergyBeeEntity bee;
    private int pollinationTicks = 0;
    private int maxPollinationTicks = 30; // Time taken to gather energy from flower
    private BlockPos targetFlowerPos;
    private boolean hasTarget = false;
    private int searchCooldown = 0;
    private int failedPollinationAttempts = 0;

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int FLOWER_SEARCH_COOLDOWN = 40; // Ticks between searches
    private static final int FLOWER_SEARCH_RANGE = 8; // Range for flower search

    public EnergyBeeChargeFromFlowerGoal(EnergyBeeEntity bee) {
        this.bee = bee;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // Don't start if bee is maxed on energy or not on ground
        if (bee.getStoredEnergy() >= bee.getMaxStoredEnergy() || failedPollinationAttempts >= MAX_FAILED_ATTEMPTS) {
            return false;
        }

        // Only search periodically
        if (searchCooldown > 0) {
            searchCooldown--;
            return false;
        }

        // Try to find a flower
        Optional<BlockPos> flower = findNearestFlower();
        if (flower.isPresent()) {
            targetFlowerPos = flower.get();
            hasTarget = true;
            return true;
        }

        // Set a cooldown before next search
        searchCooldown = FLOWER_SEARCH_COOLDOWN;
        return false;
    }

    private Optional<BlockPos> findNearestFlower() {
        World world = bee.getWorld();
        BlockPos beePos = bee.getBlockPos();

        return BlockPos.findClosest(beePos, FLOWER_SEARCH_RANGE, FLOWER_SEARCH_RANGE,
                pos -> world.getBlockState(pos).isIn(ModBlockTags.ENERGIZED_FLOWERS));
    }

    @Override
    public boolean shouldContinue() {
        // Continue if we have a valid flower target and bee still needs energy
        if (!hasTarget || bee.getStoredEnergy() >= bee.getMaxStoredEnergy()) {
            return false;
        }

        // Check if the block is still a valid flower
        if (!bee.getWorld().getBlockState(targetFlowerPos).isIn(ModBlockTags.ENERGIZED_FLOWERS)) {
            hasTarget = false;
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        pollinationTicks = 0;
        failedPollinationAttempts = 0;
        navigateToFlower();
        ApiElectric.LOGGER.info("Energy Bee targeting flower at " + targetFlowerPos);
    }

    private void navigateToFlower() {
        if (targetFlowerPos != null) {
            Vec3d targetPos = Vec3d.ofCenter(targetFlowerPos);
            bee.getNavigation().startMovingTo(targetPos.x, targetPos.y + 0.5, targetPos.z, 0.8D);
        }
    }

    @Override
    public void tick() {
        if (!hasTarget) return;

        // Check distance to flower
        double distSq = bee.getBlockPos().getSquaredDistance(targetFlowerPos);

        // If we're close enough to the flower
        if (distSq < 2.5) {
            // Look at the flower
            Vec3d flowerCenter = Vec3d.ofCenter(targetFlowerPos);
            bee.getLookControl().lookAt(flowerCenter.x, flowerCenter.y, flowerCenter.z);

            // Pollinate!
            pollinationTicks++;

            // Visual effects
            if (bee.getWorld().isClient()) {
                if (pollinationTicks % 5 == 0) {
                    bee.getWorld().addParticle(
                            ParticleTypes.ELECTRIC_SPARK,
                            bee.getX(), bee.getY() + 0.5, bee.getZ(),
                            (Math.random() - 0.5) * 0.1, 0.1, (Math.random() - 0.5) * 0.1
                    );
                }
            }

            // Once we've pollinated enough, add energy
            if (pollinationTicks >= maxPollinationTicks) {
                bee.addEnergy(10);
                ApiElectric.LOGGER.info("Energy Bee collected energy from flower. Current energy: " + bee.getStoredEnergy());
                hasTarget = false; // Reset target
            }
        }
        // If we're not close enough, keep trying to navigate
        else if (bee.getNavigation().isIdle()) {
            // Try to navigate again
            navigateToFlower();
            failedPollinationAttempts++;

            // Log the attempt
            ApiElectric.LOGGER.info("Energy Bee re-navigation attempt: " + failedPollinationAttempts);

            // If we've failed too many times, give up on this flower
            if (failedPollinationAttempts >= MAX_FAILED_ATTEMPTS) {
                ApiElectric.LOGGER.info("Energy Bee abandoning flower after too many failed attempts");
                hasTarget = false;
            }
        }
    }

    @Override
    public void stop() {
        bee.getNavigation().stop();
        pollinationTicks = 0;
        searchCooldown = 20; // Small cooldown before searching again
        ApiElectric.LOGGER.info("Energy Bee stopped charging from flower");
    }
}