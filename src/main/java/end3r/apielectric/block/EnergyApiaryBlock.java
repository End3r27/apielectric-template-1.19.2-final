package end3r.apielectric.block;

import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyApiaryBlock extends Block implements BlockEntityProvider {

    public EnergyApiaryBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Return a new instance of EnergyApiaryBlockEntity
        return new EnergyApiaryBlockEntity(pos, state);
    }
}
