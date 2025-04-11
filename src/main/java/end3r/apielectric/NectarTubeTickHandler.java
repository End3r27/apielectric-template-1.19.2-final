package end3r.apielectric;

import end3r.apielectric.block.entity.NectarTubeBlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NectarTubeTickHandler {

    public static void tick(World world, BlockPos pos) {
        var entity = world.getBlockEntity(pos);
        if (entity instanceof NectarTubeBlockEntity nectarTube) {
            nectarTube.transferEnergy(world, pos);

            // Add a particle effect when energy is transferred
            if (!world.isClient()) return; // Ensure this runs on the client side
            ClientWorld clientWorld = (ClientWorld) world;
            clientWorld.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }
}
