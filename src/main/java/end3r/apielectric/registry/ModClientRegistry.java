package end3r.apielectric.registry;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;

@Environment(EnvType.CLIENT)
public class ModClientRegistry implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        // Register screens
    }
}