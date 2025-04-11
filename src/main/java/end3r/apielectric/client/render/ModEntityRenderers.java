package end3r.apielectric.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModEntityRenderers {
    // This method should no longer be called since we're registering in ApiElectricClient
    public static void registerEntityRenderers() {
        // Registration moved to ApiElectricClient
    }
}