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
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
        this.frontLegs = root.getChild("front_legs");
        this.middleLegs = root.getChild("middle_legs");
        this.backLegs = root.getChild("back_legs");
        this.stinger = root.getChild("stinger");
        this.leftAntenna = root.getChild("left_antenna");
        this.rightAntenna = root.getChild("right_antenna");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Body
        root.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F),
                ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        // Wings - these are separate parts now
        root.addChild("right_wing",
                ModelPartBuilder.create()
                        .uv(9, 18)
                        .cuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                ModelTransform.pivot(-1.5F, 15.0F, -3.0F));

        root.addChild("left_wing",
                ModelPartBuilder.create()
                        .uv(9, 18).mirrored()
                        .cuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                ModelTransform.pivot(1.5F, 15.0F, -3.0F));

        // Legs
        root.addChild("front_legs",
                ModelPartBuilder.create()
                        .uv(26, 1)
                        .cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 0.0F),
                ModelTransform.pivot(0.0F, 22.0F, -5.0F));

        root.addChild("middle_legs",
                ModelPartBuilder.create()
                        .uv(26, 3)
                        .cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F),
                ModelTransform.pivot(0.0F, 22.0F, 0.0F));

        root.addChild("back_legs",
                ModelPartBuilder.create()
                        .uv(26, 5)
                        .cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 0.0F),
                ModelTransform.pivot(0.0F, 22.0F, 5.0F));

        // Stinger
        root.addChild("stinger",
                ModelPartBuilder.create()
                        .uv(24, 7)
                        .cuboid(0.0F, -1.0F, 5.0F, 0.0F, 2.0F, 3.0F),
                ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        // Antennae
        root.addChild("right_antenna",
                ModelPartBuilder.create()
                        .uv(2, 0)
                        .cuboid(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F),
                ModelTransform.of(-1.5F, 15.0F, -5.0F, 0.0F, -0.2618F, 0.0F));

        root.addChild("left_antenna",
                ModelPartBuilder.create()
                        .uv(2, 3)
                        .cuboid(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F),
                ModelTransform.of(1.5F, 15.0F, -5.0F, 0.0F, 0.2618F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(EnergyBeeEntity energyBee, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        // Wing flapping animation
        float wingAngle = 0.0F;
        if (energyBee.isInAir()) {
            // Fast flapping when flying
            wingAngle = MathHelper.cos(animationProgress * 2.0F) * 0.9F;
        } else {
            // Slow occasional flapping when grounded
            wingAngle = MathHelper.cos(animationProgress * 0.3F) * 0.2F;
        }

        this.rightWing.roll = wingAngle;
        this.leftWing.roll = -wingAngle;

        // Adjust body rotation based on pitch for flying
        this.body.pitch = headPitch * 0.017453292F;

        // Antenna bobbing
        float antennaBob = MathHelper.cos(animationProgress * 0.5F) * 0.1F;
        this.leftAntenna.roll = antennaBob;
        this.rightAntenna.roll = -antennaBob;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.rightWing.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leftWing.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.frontLegs.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.middleLegs.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.backLegs.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.stinger.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leftAntenna.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.rightAntenna.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}