package end3r.apielectric.bee;

import end3r.apielectric.bee.EnergyBeeEntity;
import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
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

    public GoToApiaryGoal(EnergyBeeEntity bee) {
        this.bee = bee;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Only run if bee is fully charged
        return bee.getStoredEnergy() >= bee.getMaxStoredEnergy();
    }

    @Override
    public void start() {
        World world = bee.getWorld();
        BlockPos beePos = bee.getBlockPos();

        // Search for nearby EnergyApiaryBlocks within a radius
        int radius = 10;
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
        }
    }

    @Override
    public void tick() {
        if (targetApiary != null && bee.getBlockPos().isWithinDistance(targetApiary, 2.0)) {
            BlockEntity be = bee.getWorld().getBlockEntity(targetApiary);
            if (be instanceof end3r.apielectric.block.entity.EnergyApiaryBlockEntity apiary) {
                int toTransfer = bee.getStoredEnergy();
                apiary.addHoneyCharge(toTransfer);
                bee.setStoredEnergy(0);
            }
            targetApiary = null;
        }
    }

    @Override
    public boolean shouldContinue() {
        return targetApiary != null && bee.getStoredEnergy() > 0;
    }

    @Override
    public void stop() {
        targetApiary = null;
    }
}
