package end3r.apielectric.client;

import end3r.apielectric.block.entity.HoneyPowerConsumerBlockEntity;
import end3r.apielectric.energy.IHoneyCharge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;

public class EnergyBarRenderer {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void renderEnergyBar(MatrixStack matrices) {
        BlockPos blockPos = getLookingAtBlockPos();
        if (blockPos != null && client.world.getBlockEntity(blockPos) instanceof HoneyPowerConsumerBlockEntity) {
            HoneyPowerConsumerBlockEntity blockEntity = (HoneyPowerConsumerBlockEntity) client.world.getBlockEntity(blockPos);
            IHoneyCharge honeyCharge = blockEntity;

            if (honeyCharge != null) {
                int currentEnergy = honeyCharge.getHoneyCharge();
                int maxEnergy = honeyCharge.getMaxHoneyCharge();

                // Position for the energy bar (bottom left of the screen)
                int x = 10;
                int y = client.getWindow().getScaledHeight() - 20;

                // Render the bar background
                client.getTextureManager().bindTexture(new Identifier("minecraft", "textures/gui/widgets.png"));
                drawTexturedRect(matrices, x, y, 0, 0, 100, 10);

                // Render the bar fill based on the energy percentage
                float energyPercent = (float) currentEnergy / maxEnergy;
                int width = (int) (energyPercent * 100);
                drawTexturedRect(matrices, x, y, 0, 10, width, 10);
            }
        }
    }

    // Helper method to get the block position the player is looking at
    private static BlockPos getLookingAtBlockPos() {
        // Perform a raycast to find the block the player is looking at
        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget; // Cast to BlockHitResult
            return blockHitResult.getBlockPos();
        }
        return null;
    }


    // Helper method to draw a textured rectangle (bar)
    private static void drawTexturedRect(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        client.getTextureManager().bindTexture(new Identifier("minecraft", "textures/gui/widgets.png"));
        client.inGameHud.drawTexture(matrices, x, y, u, v, width, height);
    }
}
