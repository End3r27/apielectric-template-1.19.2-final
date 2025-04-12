package end3r.apielectric.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import end3r.apielectric.ApiElectric;
import end3r.apielectric.screen.HoneyChargeFurnaceScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HoneyChargeFurnaceScreen extends HandledScreen<HoneyChargeFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ApiElectric.MOD_ID, "apielectric:textures/gui/container/honey_charge_furnace_gui.png");
    private int x;
    private int y;

    public HoneyChargeFurnaceScreen(HoneyChargeFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        x = (width - backgroundWidth) / 2;
        y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draw progress arrow
        if (handler.isCrafting()) {
            int progress = handler.getScaledProgress();
            drawTexture(matrices, x + 79, y + 34, 176, 14, progress + 1, 16);
        }

        // Draw energy bar
        int energy = handler.getScaledHoneyCharge();
        drawTexture(matrices, x + 32, y + 70 - energy, 176, 31 + (50 - energy), 12, energy);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);

        // Add tooltip for energy bar
        if (isMouseAboveArea(mouseX, mouseY, x, y, 32, 20, 12, 50)) {
            renderTooltip(matrices, Text.literal(handler.propertyDelegate.get(2) + " / " +
                    handler.propertyDelegate.get(3) + " HCU"), mouseX, mouseY);
        }
    }

    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return (mouseX >= x + offsetX && mouseX <= x + offsetX + width && mouseY >= y + offsetY && mouseY <= y + offsetY + height);
    }
}