package end3r.apielectric.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public abstract class BaseHoneyChargeBlockEntity extends BlockEntity {
    protected int storedHoneyCharge = 0;
    protected final int maxCharge;

    public BaseHoneyChargeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCharge) {
        super(type, pos, state);
        this.maxCharge = maxCharge;
    }

    public int getStoredHoneyCharge() {
        return storedHoneyCharge;
    }

    public void setStoredHoneyCharge(int amount) {
        this.storedHoneyCharge = Math.min(amount, maxCharge);
    }

    public void addHoneyCharge(int amount) {
        this.storedHoneyCharge = Math.min(this.storedHoneyCharge + amount, maxCharge);
    }

    public int consumeHoneyCharge(int amount) {
        int used = Math.min(amount, storedHoneyCharge);
        storedHoneyCharge -= used;
        return used;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("HoneyCharge", storedHoneyCharge);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedHoneyCharge = nbt.getInt("HoneyCharge");
    }
}
