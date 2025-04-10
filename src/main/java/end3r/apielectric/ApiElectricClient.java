package end3r.apielectric;

import end3r.apielectric.client.render.EnergyBeeEntityRenderer;
import end3r.apielectric.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;


@Environment(EnvType.CLIENT)
public class ApiElectricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the renderer for the EnergyBeeEntity
        EntityRendererRegistry.register(ModEntities.ENERGY_BEE, (context) -> new EnergyBeeEntityRenderer(context));
    }
}
