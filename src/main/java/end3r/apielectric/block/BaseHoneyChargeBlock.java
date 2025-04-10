package end3r.apielectric.block;

import end3r.apielectric.block.entity.BaseHoneyChargeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;


public class BaseHoneyChargeBlock extends Block implements BlockEntityProvider {

    public BaseHoneyChargeBlock(Settings settings) {
        super(settings);
    }

    // Correctly overriding createBlockEntity method from BlockEntityProvider
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BaseHoneyChargeBlockEntity();
    }
}
