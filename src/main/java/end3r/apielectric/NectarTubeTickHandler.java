package end3r.apielectric;


import end3r.apielectric.block.NectarTubeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class NectarTubeTickHandler {

    // This method will be called to transfer energy from one tube to the next.
    public static void tick(World world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof NectarTubeBlockEntity nectarTube) {
            nectarTube.transferEnergy(world, pos);
        }
    }
}
