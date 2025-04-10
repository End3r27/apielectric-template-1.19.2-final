package end3r.apielectric.client.render;

import end3r.apielectric.bee.EnergyBeeEntity;
import end3r.apielectric.registry.ModEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.BeeEntityRenderer;

@Environment(EnvType.CLIENT)
public class ModEntityRenderers {

    public static void registerEntityRenderers() {
        // Register the custom renderer for EnergyBeeEntity
        EntityRendererRegistry.register(ModEntities.ENERGY_BEE, (EntityRendererFactory.Context context) -> new BeeEntityRenderer(context));
    }
}
