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

        // Check blocks in a small radius below the bee
        for (int y = 0; y >= -2; y--) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
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
        // Ensure bee is at the right position
        if (!bee.hasAngerTime() && isFlowerBelow()) {
            // Make the bee "sit" on the flower
            bee.getMoveControl().moveTo(bee.getX(), bee.getY(), bee.getZ(), 0.3D);

            // Simple charging
            int oldEnergy = bee.getStoredEnergy();
            bee.addEnergy(5);

            // Make this more noticeable - particles, sound, etc.
            if (bee.getWorld().isClient() && oldEnergy != bee.getStoredEnergy()) {
                // Spawn particles at client side
                bee.getWorld().addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        bee.getX(), bee.getY() + 0.5, bee.getZ(),
                        0, 0.1, 0
                );
            }

            ApiElectric.LOGGER.info("Energy Bee charged: " + oldEnergy + " -> " + bee.getStoredEnergy());
        }
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