package end3r.apielectric;

import end3r.apielectric.registry.ModRegistry;
import net.fabricmc.api.ClientModInitializer;

public class ApiElectricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModRegistry.registerClient();
    }
}
