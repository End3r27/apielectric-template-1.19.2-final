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
        // Additional debug - run this every 5 seconds
        if (bee.getWorld().getTime() % 100 == 0) {
            ApiElectric.LOGGER.info("[BeeDebug] Checking if EnergyBeeChargeFromFlowerGoal can start:");
            ApiElectric.LOGGER.info("[BeeDebug] - Bee energy: " + bee.getStoredEnergy() + "/" + bee.getMaxStoredEnergy());
            ApiElectric.LOGGER.info("[BeeDebug] - Max failed attempts: " + failedPollinationAttempts + "/" + MAX_FAILED_ATTEMPTS);
            ApiElectric.LOGGER.info("[BeeDebug] - Search cooldown: " + searchCooldown);
        }

        // Don't start if bee is maxed on energy or not on ground
        if (bee.getStoredEnergy() >= bee.getMaxStoredEnergy() || failedPollinationAttempts >= MAX_FAILED_ATTEMPTS) {
            if (bee.getWorld().getTime() % 100 == 0) {
                ApiElectric.LOGGER.info("[BeeDebug] Cannot start: " +
                        (bee.getStoredEnergy() >= bee.getMaxStoredEnergy() ? "Energy maxed" : "Too many failed attempts"));
            }
            return false;
        }

        // Only search periodically
        if (searchCooldown > 0) {
            searchCooldown--;
            if (searchCooldown == 0) {
                ApiElectric.LOGGER.info("[BeeDebug] Search cooldown finished, will search for flowers");
            }
            return false;
        }

        // Try to find a flower
        ApiElectric.LOGGER.info("[BeeDebug] Searching for energized flowers...");
        Optional<BlockPos> flower = findNearestFlower();
        if (flower.isPresent()) {
            targetFlowerPos = flower.get();
            hasTarget = true;
            ApiElectric.LOGGER.info("[BeeDebug] Found flower at " + targetFlowerPos);
            return true;
        } else {
            ApiElectric.LOGGER.info("[BeeDebug] No flowers found within range");
        }

        // Set a cooldown before next search
        searchCooldown = FLOWER_SEARCH_COOLDOWN;
        return false;
    }


    private Optional<BlockPos> findNearestFlower() {
        World world = bee.getWorld();
        BlockPos beePos = bee.getBlockPos();

        ApiElectric.LOGGER.info("[BeeDebug] Scanning for flowers in range " + FLOWER_SEARCH_RANGE + " blocks from " + beePos);

        // Count the blocks with the right tag
        int foundBlocks = 0;
        BlockPos foundPos = null;

        // Manual search for better debugging
        for (BlockPos pos : BlockPos.iterateOutwards(beePos, FLOWER_SEARCH_RANGE, FLOWER_SEARCH_RANGE, FLOWER_SEARCH_RANGE)) {
            if (world.getBlockState(pos).isIn(ModBlockTags.ENERGIZED_FLOWERS)) {
                foundBlocks++;
                if (foundPos == null) {
                    foundPos = pos.toImmutable();
                }
                // Only log the first 5 to avoid spam
                if (foundBlocks <= 5) {
                    ApiElectric.LOGGER.info("[BeeDebug] Found energized flower at " + pos);
                }
            }
        }

        ApiElectric.LOGGER.info("[BeeDebug] Found " + foundBlocks + " energized flowers in range");

        if (foundPos != null) {
            return Optional.of(foundPos);
        }

        return Optional.empty();
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
        ApiElectric.LOGGER.info("[BeeDebug] Starting navigation to flower at " + targetFlowerPos);
        ApiElectric.LOGGER.info("[BeeDebug] Bee position: " + bee.getBlockPos() + ", Distance: " +
                Math.sqrt(bee.getBlockPos().getSquaredDistance(targetFlowerPos)));

        navigateToFlower();
    }

    private void navigateToFlower() {
        if (targetFlowerPos != null) {
            Vec3d targetPos = Vec3d.ofCenter(targetFlowerPos);
            ApiElectric.LOGGER.info("[BeeDebug] Setting navigation target to " + targetPos);
            boolean success = bee.getNavigation().startMovingTo(targetPos.x, targetPos.y + 0.5, targetPos.z, 0.8D);
            ApiElectric.LOGGER.info("[BeeDebug] Navigation start result: " + (success ? "success" : "failed"));
        }
    }

    @Override
    public void tick() {
        if (!hasTarget) return;

        // Check distance to flower - INCREASED FROM 2.5 to 3.5 for better detection
        double distSq = bee.getBlockPos().getSquaredDistance(targetFlowerPos);

        // Debug distance output occasionally
        if (bee.getWorld().getTime() % 100 == 0) {
            ApiElectric.LOGGER.info("Energy Bee distance to flower: " + Math.sqrt(distSq) + ", position: " + bee.getBlockPos() + ", target: " + targetFlowerPos);
        }

        // If we're close enough to the flower - INCREASED DISTANCE CHECK
        if (distSq < 3.5) {
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
            // Try to navigate again with improved targeting
            Vec3d flowerPos = Vec3d.ofCenter(targetFlowerPos);
            // Target slightly above the flower for better positioning
            bee.getNavigation().startMovingTo(flowerPos.x, flowerPos.y + 0.5, flowerPos.z, 0.8D);
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