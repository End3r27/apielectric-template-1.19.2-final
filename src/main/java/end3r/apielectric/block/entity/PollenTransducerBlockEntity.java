package end3r.apielectric.block.entity;

import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PollenTransducerBlockEntity extends BaseHoneyChargeBlockEntity {

    public static final int MAX_HONEYCHARGE = 5000;

    public PollenTransducerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POLLEN_TRANS_ENTITY, pos, state, MAX_HONEYCHARGE);
    }
}
