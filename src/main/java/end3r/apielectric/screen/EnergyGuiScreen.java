package end3r.apielectric.screen;

import end3r.apielectric.block.entity.BaseHoneyChargeBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnergyGuiScreen extends DrawableHelper {

    private static final Identifier ENERGY_ICON = new Identifier("apielectric", "textures/gui/energy_icon.png");
    private final BaseHoneyChargeBlockEntity blockEntity;

    public EnergyGuiScreen(BaseHoneyChargeBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int energyBarX = 10; // X position for the energy bar
        int energyBarY = 10; // Y position for the energy bar
        int energyBarWidth = 100; // Width of the energy bar
        int energyBarHeight = 10; // Height of the energy bar

        // Render the energy icon
        client.getTextureManager().bindTexture(ENERGY_ICON);
        drawTexture(matrices, energyBarX - 20, energyBarY, 0, 0, 16, 16, 16, 16);

        // Render the energy progress bar
        int filledWidth = MathHelper.ceil(energyBarWidth * (blockEntity.getStoredHoneyCharge() / (float) blockEntity.getMaxCharge()));
        fill(matrices, energyBarX, energyBarY, energyBarX + filledWidth, energyBarY + energyBarHeight, 0xFF00FF00); // Green bar for filled portion
        fill(matrices, energyBarX + filledWidth, energyBarY, energyBarX + energyBarWidth, energyBarY + energyBarHeight, 0xFFAAAAAA); // Grey for empty portion

        // Render energy text
        String energyText = "Energy: " + blockEntity.getStoredHoneyCharge() + " / " + blockEntity.getMaxCharge() + " HC";
        textRenderer.draw(matrices, Text.of(energyText), energyBarX + 20, energyBarY - 12, 0xFFFFFF);
    }
}