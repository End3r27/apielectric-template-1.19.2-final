package end3r.apielectric.bee;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class EnergyBeeEntityModel extends EntityModel<EnergyBeeEntity> {
    private final ModelPart body;
    private final ModelPart wings;

    public EnergyBeeEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.wings = root.getChild("wings");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Add body part
        modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 16.0F),
                ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        // Add wings part
        modelPartData.addChild("wings",
                ModelPartBuilder.create()
                        .uv(32, 0)
                        .cuboid(-8.0F, -4.0F, -4.0F, 16.0F, 0.0F, 8.0F),
                ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(EnergyBeeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Make the wings flap based on the bee's movement
        this.wings.roll = MathHelper.sin(animationProgress * 0.5f) * 0.3f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // Render the body and wings
        this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.wings.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}