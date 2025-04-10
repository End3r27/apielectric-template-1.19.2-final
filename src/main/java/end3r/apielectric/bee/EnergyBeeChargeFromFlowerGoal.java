package end3r.apielectric.bee;

import end3r.apielectric.registry.ModBlockTags;
import end3r.apielectric.registry.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class EnergyBeeChargeFromFlowerGoal extends MoveToTargetPosGoal {
    private final EnergyBeeEntity bee;

    public EnergyBeeChargeFromFlowerGoal(EnergyBeeEntity bee) {
        super(bee, 1.0D, 8);
        this.bee = bee;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.getBlockState(pos).isIn(ModBlockTags.ENERGIZED_FLOWERS); // Or tag/custom flower later
    }

    @Override
    public void tick() {
        super.tick();
        if (hasReached()) {
            bee.addEnergy(5); // Slowly charge energy
        }
    }

}
