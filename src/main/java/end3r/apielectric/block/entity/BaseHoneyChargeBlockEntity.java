package end3r.apielectric.block.entity;

import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class BaseHoneyChargeBlockEntity extends BlockEntity {

    // Define your stored honey charge and maximum charge here
    private int storedHoneyCharge = 0;
    private final int maxHoneyCharge;

    // Constructor that takes BlockEntityType, position, state, and max charge
    public BaseHoneyChargeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxHoneyCharge) {
        super(type, pos, state);
        this.maxHoneyCharge = maxHoneyCharge;
    }

    // Constructor for the base block itself
    public BaseHoneyChargeBlockEntity(BlockPos pos, BlockState state) {
        // When used directly, use the ModBlockEntities.BASE_HONEY_CHARGE_BLOCK_ENTITY type
        this(ModBlockEntities.BASE_HONEY_CHARGE_BLOCK_ENTITY, pos, state, 10000);
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
        return maxHoneyCharge;
    }

    // Add honey charge to the block entity, respecting the max charge
// Returns the amount of energy actually added
    public int addHoneyCharge(int amount) {
        int previousCharge = this.storedHoneyCharge;
        this.storedHoneyCharge = Math.min(storedHoneyCharge + amount, maxHoneyCharge);
        return this.storedHoneyCharge - previousCharge; // Return how much was actually added
    }

    // Consume honey charge from the block entity
    public int consumeHoneyCharge(int amount) {
        int consumed = Math.min(storedHoneyCharge, amount);
        storedHoneyCharge -= consumed;
        return consumed;
    }
}