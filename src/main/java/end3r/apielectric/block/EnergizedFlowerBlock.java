package end3r.apielectric.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class EnergizedFlowerBlock extends Block {
    public EnergizedFlowerBlock(Settings settings) {
        super(settings);
    }


    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Emit some electric spark particles around the flower
        for (int i = 0; i < 2; i++) {
            double offsetX = random.nextFloat();
            double offsetY = 0.7 + random.nextFloat() * 0.3;
            double offsetZ = random.nextFloat();
            world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    pos.getX() + offsetX,
                    pos.getY() + offsetY,
                    pos.getZ() + offsetZ,
                    0.0D, 0.01D, 0.0D);
        }
    }


    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.isOf(this);
    }
}
