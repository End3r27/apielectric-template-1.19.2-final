package end3r.apielectric.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltipItem extends Item {
    private final String tooltipKey;

    public TooltipItem(String tooltipKey, Settings settings) {
        super(settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.apielectric." + tooltipKey + ".advanced").formatted(Formatting.GOLD));
        } else {
            tooltip.add(Text.translatable("tooltip.apielectric." + tooltipKey).formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Hold Shift for more info").formatted(Formatting.BLUE));
        }
    }
}