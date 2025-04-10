package end3r.apielectric.bee;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

// The renderer that uses this model
public class EnergyBeeEntityRenderer extends MobEntityRenderer<EnergyBeeEntity, EnergyBeeModel> {
    private static final Identifier TEXTURE = new Identifier("apielectric", "textures/entity/bee/energy_bee.png");

    public EnergyBeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EnergyBeeModel(context.getPart(
                new EntityModelLayer(new Identifier("apielectric", "energy_bee"), "main"))), 0.4F);
    }

    @Override
    public Identifier getTexture(EnergyBeeEntity entity) {
        return TEXTURE;
    }
}
