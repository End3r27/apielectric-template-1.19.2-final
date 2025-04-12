package end3r.apielectric;

import end3r.apielectric.block.EnergizedFlowerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import java.util.Random;

public class BlockEventHandler {

    public static void triggerRandomTick(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof EnergizedFlowerBlock) {
            EnergizedFlowerBlock block = (EnergizedFlowerBlock) state.getBlock();
            Random random = new Random();  // Or you can pass a more appropriate Random source
            block.randomDisplayTick(state, world, pos, random);  // Call the randomDisplayTick method
        }
    }
}
