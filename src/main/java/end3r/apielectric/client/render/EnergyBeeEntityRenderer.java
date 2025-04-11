package end3r.apielectric.client.render;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.bee.EnergyBeeEntity;
import end3r.apielectric.client.model.EnergyBeeModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class EnergyBeeEntityRenderer extends MobEntityRenderer<EnergyBeeEntity, EnergyBeeModel> {
    private static final Identifier TEXTURE = new Identifier(ApiElectric.MOD_ID, "textures/entity/bee/energy_bee.png");
    private static final Identifier ANGRY_TEXTURE = new Identifier(ApiElectric.MOD_ID, "textures/entity/bee/energy_bee_angry.png");
    private static final Identifier CHARGING_TEXTURE = new Identifier(ApiElectric.MOD_ID, "textures/entity/bee/energy_bee_charged.png");

    public EnergyBeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EnergyBeeModel(context.getPart(EnergyBeeModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public Identifier getTexture(EnergyBeeEntity entity) {
        // Return different textures based on energy level if you have them
        if (entity.getStoredEnergy() > entity.getMaxStoredEnergy() * 0.75) {
            return CHARGING_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(EnergyBeeEntity entity, MatrixStack matrices, float animationProgress,
                                   float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);

        // Apply bobbing effect for flight
        if (entity.isInAir()) {
            float bobbing = MathHelper.cos(animationProgress * 0.18F) * 0.05F;
            matrices.translate(0.0, bobbing + 0.1F, 0.0);
        }
    }
}