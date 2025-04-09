package end3r.apielectric.item;

import end3r.apielectric.energy.IHoneyCharge;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.world.World;

import java.util.List;

public class HoneyChargeItem extends Item implements IHoneyCharge {

    private int maxCharge;

    public HoneyChargeItem(int maxCharge, Settings settings) {
        super(settings);
        this.maxCharge = maxCharge;
    }

    @Override
    public int getHoneyCharge() {
        return this.maxCharge;
    }

    @Override
    public int getMaxHoneyCharge() {
        return this.maxCharge;
    }

    @Override
    public boolean canExtract() {
        return true; // HoneyChargeItem can extract energy
    }

    @Override
    public boolean canReceive() {
        return true; // HoneyChargeItem can receive energy
    }

    @Override
    public int addHoneyCharge(int amount) {
        // Adding charge logic if necessary
        if (this.maxCharge + amount > 10000) {
            this.maxCharge = 10000;
        } else {
            this.maxCharge += amount;
        }
        return amount;
    }

    @Override
    public int extractHoneyCharge(int amount) {
        // Extract HoneyCharge and return the amount transferred
        if (this.maxCharge >= amount) {
            this.maxCharge -= amount;
            return amount;
        }
        return 0;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.literal("HoneyCharge: " + getHoneyCharge()));
    }
}
