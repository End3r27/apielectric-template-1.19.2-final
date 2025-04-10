
package end3r.apielectric.block.entity;

import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CombCapacitorBlockEntity extends BaseHoneyChargeBlockEntity {

    public static final int MAX_HONEYCHARGE = 8000;

    public CombCapacitorBlockEntity(net.minecraft.util.math.BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMB_CAPACITOR_ENTITY, pos, state, MAX_HONEYCHARGE);
    }
}
