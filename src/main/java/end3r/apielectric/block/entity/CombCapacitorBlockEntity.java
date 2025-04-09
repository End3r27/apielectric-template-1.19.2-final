package end3r.apielectric.block.entity;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CombCapacitorBlockEntity extends BlockEntity {

    private int storedEnergy;

    // Updated constructor with BlockState argument
    public CombCapacitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMB_CAPACITOR_ENTITY, pos, state);  // Pass state to super constructor
        this.storedEnergy = 0;  // Initial energy state
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("StoredEnergy", this.storedEnergy);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.storedEnergy = tag.getInt("StoredEnergy");
    }

    public void addEnergy(int amount) {
        this.storedEnergy += amount;
        // Add energy cap logic if needed
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }
}