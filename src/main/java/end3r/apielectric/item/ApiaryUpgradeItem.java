package end3r.apielectric.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ApiaryUpgradeItem extends Item {

    public enum UpgradeType {
        ENERGIZED_FRAME,
        WAX_SHIELDING,
        APIARY_CAPACITOR
    }

    private final UpgradeType upgradeType;
    private final String description;

    public ApiaryUpgradeItem(Settings settings, UpgradeType upgradeType, String description) {
        super(settings);
        this.upgradeType = upgradeType;
        this.description = description;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(description).formatted(Formatting.GRAY));

        // Add effect description
        switch (upgradeType) {
            case ENERGIZED_FRAME:
                tooltip.add(Text.literal("Doubles HoneyCharge generation per bee").formatted(Formatting.YELLOW));
                break;
            case WAX_SHIELDING:
                tooltip.add(Text.literal("Makes the hive resistant to explosions").formatted(Formatting.BLUE));
                break;
            case APIARY_CAPACITOR:
                tooltip.add(Text.literal("Increases hive's internal HoneyCharge storage").formatted(Formatting.GREEN));
                break;
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}