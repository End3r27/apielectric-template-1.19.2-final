package end3r.apielectric.screen;

import end3r.apielectric.block.entity.HoneyPowerConsumerBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HoneyPowerConsumerScreen extends HandledScreen<HoneyPowerConsumerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("apielectric", "textures/gui/honey_power_consumer.png");

    public HoneyPowerConsumerScreen(HoneyPowerConsumerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderBackground(matrices);
        client.getTextureManager().bindTexture(TEXTURE);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draw energy bar (example)
        int energy = handler.getBlockEntity().getHoneyCharge();
        int max = handler.getBlockEntity().getMaxHoneyCharge();
        int barHeight = (int)(energy / (float)max * 50);
        drawTexture(matrices, x + 80, y + 20 + (50 - barHeight), 176, 0, 14, barHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        drawTextWithShadow(matrices, textRenderer, title, 8, 6, 0x404040);
        drawTextWithShadow(matrices, textRenderer, Text.literal("Charge: " + handler.getBlockEntity().getHoneyCharge()), 8, 70, 0x808000);

    }
}
