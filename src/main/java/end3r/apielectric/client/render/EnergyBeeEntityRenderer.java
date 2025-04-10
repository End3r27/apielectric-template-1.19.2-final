package end3r.apielectric.client.render;

import end3r.apielectric.bee.EnergyBeeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;

public class EnergyBeeEntityRenderer extends EntityRenderer<EnergyBeeEntity> {

    private static final Identifier TEXTURE = new Identifier("apielectric", "textures/entity/energy_bee.png");

    public EnergyBeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(EnergyBeeEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        // Render the EnergyBeeEntity here

        // Use the Minecraft client model and rendering system for rendering
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(EnergyBeeEntity entity) {
        return TEXTURE; // Return the texture path for the EnergyBeeEntity
    }
}
