package end3r.apielectric.client.model;

import end3r.apielectric.bee.EnergyBeeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnergyBeeModel extends EntityModel<EnergyBeeEntity> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
            new Identifier("apielectric", "energy_bee"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public EnergyBeeModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.leftWing = body.getChild("left_wing");
        this.rightWing = body.getChild("right_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F),
                ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        body.addChild("left_wing",
                ModelPartBuilder.create()
                        .uv(0, 18).mirrored()
                        .cuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                ModelTransform.of(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F));

        body.addChild("right_wing",
                ModelPartBuilder.create()
                        .uv(0, 18)
                        .cuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                ModelTransform.of(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F));

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(EnergyBeeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float flap = MathHelper.cos(animationProgress * 2.0F) * 3.1415F * 0.15F;
        this.leftWing.roll = -flap;
        this.rightWing.roll = flap;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
