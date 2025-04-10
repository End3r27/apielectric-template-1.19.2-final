package end3r.apielectric.bee;

import end3r.apielectric.registry.ModEntities;
import end3r.apielectric.registry.ModelBees;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

// The renderer that uses this model
public class EnergyBeeEntityRenderer extends MobEntityRenderer<EnergyBeeEntity, EnergyBeeModel> {
    private static final Identifier TEXTURE = new Identifier("apielectric", "textures/entity/bee/energy_bee.png");

    public EnergyBeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EnergyBeeModel(context.getPart(ModelBees.ENERGY_BEE_LAYER)), 0.4F);
    }

    @Override
    public Identifier getTexture(EnergyBeeEntity entity) {
        return TEXTURE;
    }
    // Inside EnergyBeeEntityRenderer.java
// EnergyBeeEntityRenderer.java
    public static void register() {
        EntityRendererRegistry.register(ModEntities.ENERGY_BEE, EnergyBeeEntityRenderer::new);

        EntityModelLayer modelLayer = new EntityModelLayer(
                new Identifier("apielectric", "energy_bee"),
                "main"
        );

        EntityModelLayerRegistry.registerModelLayer(modelLayer, EnergyBeeModel::getModelData);
    }
}
