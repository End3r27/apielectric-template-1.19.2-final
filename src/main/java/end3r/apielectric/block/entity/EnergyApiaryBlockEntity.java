package end3r.apielectric.block.entity;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class EnergyApiaryBlockEntity extends BlockEntity {

    private int storedHoneyCharge = 0;
    private static final int MAX_HONEYCHARGE = 10000;

    // Constructor that includes BlockPos and BlockState
    public EnergyApiaryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_APIARY_ENTITY, pos, state); // Pass BlockPos and BlockState to super constructor
    }

    public void addHoneyCharge(int amount) {
        storedHoneyCharge = Math.min(storedHoneyCharge + amount, MAX_HONEYCHARGE);
    }

    public int consumeHoneyCharge(int amount) {
        int consumed = Math.min(storedHoneyCharge, amount);
        storedHoneyCharge -= consumed;
        return consumed;
    }

    public int getStoredHoneyCharge() {
        return storedHoneyCharge;
    }
}
