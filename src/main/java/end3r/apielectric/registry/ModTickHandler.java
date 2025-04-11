package end3r.apielectric.registry;

import end3r.apielectric.NectarTubeTickHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;

public class ModTickHandler implements ServerTickEvents.StartTick {

    @Override
    public void onStartTick(MinecraftServer server) {
        // Iterate over all worlds to find NectarTube blocks and update them
        server.getWorlds().forEach(world -> {
            for (BlockPos pos : getAllNectarTubePositions(world)) {
                NectarTubeTickHandler.tick(world, pos); // Call the Nectar Tube handler to transfer energy
            }
        });
    }

    private List<BlockPos> getAllNectarTubePositions(World world) {
        // Ideally, you will need to track and find all the NectarTube positions in the world
        // For now, we're just returning a sample list
        return List.of(new BlockPos(0, 0, 0));  // Replace with real block location detection
    }
}
