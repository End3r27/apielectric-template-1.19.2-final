package end3r.apielectric.block.entity;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class EnergyApiaryBlockEntity extends BaseHoneyChargeBlockEntity {
    public EnergyApiaryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_APIARY_ENTITY, pos, state, 10000);
    }
}
