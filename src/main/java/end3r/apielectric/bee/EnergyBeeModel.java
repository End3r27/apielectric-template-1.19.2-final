package end3r.apielectric.bee;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.EntityModelLayer;

// Custom bee model for energy bee
public class EnergyBeeModel extends EntityModel<EnergyBeeEntity> {
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;

    public EnergyBeeModel(ModelPart root) {
        this.body = root.getChild("body");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.frontLegs = this.body.getChild("front_legs");
        this.middleLegs = this.body.getChild("middle_legs");
        this.backLegs = this.body.getChild("back_legs");
        this.stinger = this.body.getChild("stinger");
        this.leftAntenna = this.body.getChild("left_antenna");
        this.rightAntenna = this.body.getChild("right_antenna");
    }

    public static TexturedModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData body = modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F),
                ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        body.addChild("right_wing",
                ModelPartBuilder.create()
                        .uv(0, 18)
                        .cuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                ModelTransform.of(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F));

        body.addChild("left_wing",
                ModelPartBuilder.create()
                        .uv(0, 18).mirrored()
                        .cuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                ModelTransform.of(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F));

        body.addChild("front_legs",
                ModelPartBuilder.create()
                        .uv(26, 1)
                        .cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 0.0F),
                ModelTransform.pivot(0.0F, 3.0F, -2.0F));

        body.addChild("middle_legs",
                ModelPartBuilder.create()
                        .uv(26, 3)
                        .cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F),
                ModelTransform.pivot(0.0F, 3.0F, 0.0F));

        body.addChild("back_legs",
                ModelPartBuilder.create()
                        .uv(26, 5)
                        .cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 0.0F),
                ModelTransform.pivot(0.0F, 3.0F, 2.0F));

        body.addChild("stinger",
                ModelPartBuilder.create()
                        .uv(24, 7)
                        .cuboid(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 2.0F),
                ModelTransform.pivot(0.0F, 0.0F, 5.0F));

        body.addChild("left_antenna",
                ModelPartBuilder.create()
                        .uv(2, 0)
                        .cuboid(0.0F, -2.0F, -3.0F, 0.0F, 2.0F, 3.0F),
                ModelTransform.pivot(1.5F, -4.0F, -5.0F));

        body.addChild("right_antenna",
                ModelPartBuilder.create()
                        .uv(2, 3)
                        .cuboid(0.0F, -2.0F, -3.0F, 0.0F, 2.0F, 3.0F),
                ModelTransform.pivot(-1.5F, -4.0F, -5.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(EnergyBeeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Animation code - wing flapping, etc.
        this.rightWing.roll = 0.0F;
        this.leftAntenna.roll = 0.0F;
        this.rightAntenna.roll = 0.0F;

        float f = animationProgress * 2.1F;
        this.rightWing.yaw = 0.0F;
        this.leftWing.yaw = 0.0F;
        this.rightWing.roll = MathHelper.cos(f) * 3.1415927F * 0.15F;
        this.leftWing.roll = -this.rightWing.roll;

        this.frontLegs.pitch = 0.0F;
        this.middleLegs.pitch = 0.0F;
        this.backLegs.pitch = 0.0F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

