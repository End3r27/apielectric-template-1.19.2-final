package end3r.apielectric.item;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public class TooltipBlockItem extends BlockItem {
    private final String tooltipKey;

    public TooltipBlockItem(Block block, String tooltipKey, Settings settings) {
        super(block, settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // First add the basic tooltip text
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.apielectric." + tooltipKey + ".advanced").formatted(Formatting.GOLD));

            // If this block stores HoneyCharge, display the charge information
            if (hasHoneyCharge(stack)) {
                // Retrieve the stored honey charge from the NBT data
                NbtCompound nbt = stack.getOrCreateNbt();
                int storedHoneyCharge = nbt.getInt("HoneyCharge");
                int maxHoneyCharge = getMaxHoneyCharge(tooltipKey);

                int percent = (int) ((storedHoneyCharge / (float) maxHoneyCharge) * 100);
                Formatting color = percent > 66 ? Formatting.GREEN : percent > 33 ? Formatting.YELLOW : Formatting.RED;

                // Add the energy icon and stored charge in tooltip
                tooltip.add(Text.literal("Energy: ").formatted(Formatting.GRAY)
                        .append(Text.literal("HC").formatted(Formatting.YELLOW)));
                tooltip.add(Text.literal("Stored HoneyCharge: " + storedHoneyCharge + " / " + maxHoneyCharge + " HC")
                        .formatted(color));

                // Display progress bar
                int bars = Math.round((percent / 10f));
                String bar = "█".repeat(bars) + "░".repeat(10 - bars);
                tooltip.add(Text.literal(bar).formatted(color));
            }
        } else {
            tooltip.add(Text.translatable("tooltip.apielectric." + tooltipKey).formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Hold Shift for more info").formatted(Formatting.BLUE));
        }
    }

    private boolean hasHoneyCharge(ItemStack stack) {
        // Blocks that can store HoneyCharge
        return tooltipKey.equals("energy_apiary") ||
                tooltipKey.equals("comb_capacitor") ||
                tooltipKey.equals("honey_charge_conduit") ||
                tooltipKey.equals("honey_charge_furnace") ||
                tooltipKey.equals("pollen_transducer");
    }

    private int getMaxHoneyCharge(String tooltipKey) {
        switch (tooltipKey) {
            case "energy_apiary": return 5000;
            case "comb_capacitor": return 40000;
            case "honey_charge_conduit": return 2000;
            case "honey_charge_furnace": return 10000;
            case "pollen_transducer": return 20000;
            default: return 10000;
        }
    }
}