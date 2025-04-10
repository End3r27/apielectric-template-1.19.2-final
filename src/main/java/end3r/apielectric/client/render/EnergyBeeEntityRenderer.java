package end3r.apielectric.client.render;

import end3r.apielectric.bee.EnergyBeeEntity;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class EnergyBeeEntityRenderer extends BeeEntityRenderer {

    private static final Identifier TEXTURE = new Identifier("apielectric", "textures/entity/energy_bee.png");

    public EnergyBeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        EnergyBeeTextureHelper.getEnergyBeeTexture();
    }


}
