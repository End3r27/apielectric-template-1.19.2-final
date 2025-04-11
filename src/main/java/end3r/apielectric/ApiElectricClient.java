package end3r.apielectric;

import end3r.apielectric.client.model.EnergyBeeModel;
import end3r.apielectric.client.render.EnergyBeeEntityRenderer;
import end3r.apielectric.client.screen.HoneyChargeFurnaceScreen;
import end3r.apielectric.registry.ModBlocks;
import end3r.apielectric.registry.ModEntities;
import end3r.apielectric.registry.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class ApiElectricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the model layer first
        EntityModelLayerRegistry.registerModelLayer(EnergyBeeModel.LAYER_LOCATION, EnergyBeeModel::getTexturedModelData);

        // Register the renderer for the EnergyBeeEntity
        EntityRendererRegistry.register(ModEntities.ENERGY_BEE, EnergyBeeEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HONEY_CHARGE_CONDUIT, RenderLayer.getTranslucent());

        net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.register(
                ModScreenHandlers.HONEY_CHARGE_FURNACE_SCREEN_HANDLER,
                HoneyChargeFurnaceScreen::new
        );
    }
}