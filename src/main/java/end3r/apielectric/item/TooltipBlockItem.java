package end3r.apielectric.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;

public class TooltipBlockItem extends BlockItem {
    private final String tooltipKey;

    public TooltipBlockItem(Block block, String tooltipKey, Settings settings) {
        super(block, settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (hasShiftDown()) {
            tooltip.add(Text.translatable(tooltipKey + ".advanced").formatted(Formatting.YELLOW));
        } else {
            tooltip.add(Text.translatable(tooltipKey).formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Hold \u00A7eShift\u00A7r for more info").formatted(Formatting.DARK_GRAY));
        }
    }

    private boolean hasShiftDown() {
        // Only works client-side with context
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
