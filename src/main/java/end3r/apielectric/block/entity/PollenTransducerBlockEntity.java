package end3r.apielectric.block.entity;

import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PollenTransducerBlockEntity extends BlockEntity {

    // Define any properties you need
    private int storedPollenEnergy;

    // Constructor that includes BlockPos and BlockState
    public PollenTransducerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POLLEN_TRANS_ENTITY, pos, state); // Pass BlockPos and BlockState to super constructor
    }

    // Add any additional methods for functionality
    public void addPollenEnergy(int amount) {
        this.storedPollenEnergy += amount;
    }

    public int getStoredPollenEnergy() {
        return this.storedPollenEnergy;
    }
}
