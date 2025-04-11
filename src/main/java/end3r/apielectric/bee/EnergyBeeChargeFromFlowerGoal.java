// Replace or modify your EnergyBeeChargeFromFlowerGoal.java
package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockTags;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class EnergyBeeChargeFromFlowerGoal extends Goal {
    private final EnergyBeeEntity bee;
    private int cooldown = 0;

    public EnergyBeeChargeFromFlowerGoal(EnergyBeeEntity bee) {
        this.bee = bee;
        this.setControls(EnumSet.of(Control.MOVE));
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
        // Simple charging
        int oldEnergy = bee.getStoredEnergy();
        bee.addEnergy(5);

        ApiElectric.LOGGER.info("Energy Bee charged: " + oldEnergy + " -> " + bee.getStoredEnergy());

        // Continue for a moment then stop
        if (bee.getWorld().getRandom().nextInt(20) == 0) {
            stop();
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