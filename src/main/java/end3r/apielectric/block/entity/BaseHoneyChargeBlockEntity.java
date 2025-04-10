package end3r.apielectric.block.entity;

import end3r.apielectric.ApiElectric;
import net.minecraft.block.entity.BlockEntity;

public class BaseHoneyChargeBlockEntity extends BlockEntity {

    // Define your stored honey charge and maximum charge here
    private int storedHoneyCharge = 0;
    private static final int MAX_HONEYCHARGE = 10000; // Set your max charge value here

    public BaseHoneyChargeBlockEntity() {
        super(ApiElectric.BASE_HONEY_CHARGE_BLOCK_ENTITY);
    }

    // Get the current stored honey charge
    public int getStoredHoneyCharge() {
        return storedHoneyCharge;
    }

    // Set the stored honey charge
    public void setStoredHoneyCharge(int storedHoneyCharge) {
        this.storedHoneyCharge = storedHoneyCharge;
    }

    // Get the maximum charge this block can hold
    public int getMaxCharge() {
        return MAX_HONEYCHARGE;
    }

    // Add honey charge to the block entity, respecting the max charge
    public void addHoneyCharge(int amount) {
        this.storedHoneyCharge = Math.min(storedHoneyCharge + amount, MAX_HONEYCHARGE);
    }

    // Consume honey charge from the block entity
    public int consumeHoneyCharge(int amount) {
        int consumed = Math.min(storedHoneyCharge, amount);
        storedHoneyCharge -= consumed;
        return consumed;
    }
}
