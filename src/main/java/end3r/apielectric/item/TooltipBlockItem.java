package end3r.apielectric.item;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;

public class TooltipBlockItem extends BlockItem {
    private final String tooltipKey;

    public TooltipBlockItem(Block block, String tooltipKey, Settings settings) {
        super(block, settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            // Retrieve the BlockEntity's HoneyCharge directly from the NBT data on the ItemStack
            NbtCompound nbt = stack.getOrCreateNbt();
            int storedHoneyCharge = nbt.getInt("HoneyCharge");
            int maxHoneyCharge = 10000; // This should match the maximum charge for your block

            int percent = (int) ((storedHoneyCharge / (float) maxHoneyCharge) * 100);
            Formatting color = percent > 66 ? Formatting.GREEN : percent > 33 ? Formatting.YELLOW : Formatting.RED;

            tooltip.add(Text.literal("Stored HoneyCharge: " + storedHoneyCharge + " / " + maxHoneyCharge + " HC").formatted(color));

            // Add a text-based progress bar (10 blocks wide)
            int bars = Math.round((percent / 10f));
            String bar = "█".repeat(bars) + "░".repeat(10 - bars);
            tooltip.add(Text.literal(bar).formatted(color));
        } else {
            tooltip.add(Text.literal("Hold Shift for charge info").formatted(Formatting.GRAY));
        }
    }



    private boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    private int getHoneyCharge(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.getInt("HoneyCharge");
    }
}
