package end3r.apielectric.client.render;

import end3r.apielectric.bee.EnergyBeeEntity;
import end3r.apielectric.client.model.EnergyBeeModel;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

// Make sure this matches your entity
public class EnergyBeeEntityRenderer extends MobEntityRenderer<EnergyBeeEntity, EnergyBeeModel> {
    public EnergyBeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EnergyBeeModel(context.getPart(EnergyBeeModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public Identifier getTexture(EnergyBeeEntity entity) {
        return new Identifier("apielectric", "textures/entity/bee/energy_bee.png");
    }
}
