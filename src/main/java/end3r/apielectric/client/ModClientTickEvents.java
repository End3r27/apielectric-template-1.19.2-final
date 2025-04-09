package end3r.apielectric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModClientTickEvents implements ClientModInitializer {

    private static final Logger LOGGER = LogManager.getLogger(ModClientTickEvents.class);
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                LOGGER.info("Client tick end: Rendering energy bar.");
                LOGGER.debug("Player Position: " + client.player.getBlockPos());
                MatrixStack matrices = new MatrixStack();
                EnergyBarRenderer.renderEnergyBar(matrices);
            } else {
                if(client.world == null) {
                    LOGGER.warn("Client world is null; cannot render energy bar.");
                }
                if(client.player == null) {
                    LOGGER.warn("Client player is null; cannot render energy bar.");
                }
            }
        });
    }
}
