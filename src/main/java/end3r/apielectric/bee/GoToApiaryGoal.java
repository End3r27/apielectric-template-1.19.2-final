package end3r.apielectric.bee;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class GoToApiaryGoal extends Goal {

    private final EnergyBeeEntity bee;
    private BlockPos targetApiary;
    private int searchCooldown = 0;

    public GoToApiaryGoal(EnergyBeeEntity bee) {
        this.bee = bee;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Only run if bee is charged enough
        boolean energyCondition = bee.getStoredEnergy() >= bee.getMaxStoredEnergy() * 0.5; // Reduced threshold to 50%

        // Add debug output
        if (bee.getWorld().getTime() % 200 == 0) { // Log every 10 seconds roughly
        }

        return energyCondition;
    }

    @Override
    public void start() {
        World world = bee.getWorld();
        BlockPos beePos = bee.getBlockPos();
        targetApiary = null;


        // Search for nearby EnergyApiaryBlocks within a radius
        int radius = 30;
        for (BlockPos pos : BlockPos.iterateOutwards(beePos, radius, radius / 2, radius)) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == ModBlocks.ENERGY_APIARY) {
                targetApiary = pos.toImmutable();
                break;
            }
        }

        // Start flying to it
        if (targetApiary != null) {
            Vec3d target = Vec3d.ofCenter(targetApiary);
            bee.getNavigation().startMovingTo(target.x, target.y, target.z, 1.0D);
        } else {
        }
    }

    @Override
    public void tick() {
        if (targetApiary == null) return;

        if (bee.getBlockPos().isWithinDistance(targetApiary, 2.0)) {

            BlockEntity be = bee.getWorld().getBlockEntity(targetApiary);
            if (be instanceof end3r.apielectric.block.entity.EnergyApiaryBlockEntity apiary) {
                int toTransfer = bee.getStoredEnergy();
                apiary.receiveEnergy(toTransfer); // Use receiveEnergy instead
                bee.setStoredEnergy(0);
            } else {
            }
            targetApiary = null;
        } else if (bee.getNavigation().isIdle() && targetApiary != null) {
            // If navigation stopped but we haven't reached the target, try again
            Vec3d target = Vec3d.ofCenter(targetApiary);
            bee.getNavigation().startMovingTo(target.x, target.y, target.z, 1.0D);
        }
    }

    @Override
    public boolean shouldContinue() {
        return targetApiary != null && bee.getStoredEnergy() > 0 && !bee.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        targetApiary = null;
    }
}