package end3r.apielectric.block;

import end3r.apielectric.block.entity.HoneyChargeConduitBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HoneyChargeConduitBlock extends BlockWithEntity {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public HoneyChargeConduitBlock(Settings settings) {
        super(Settings.of(Material.GLASS)
                .strength(1.0F, 1.0F)
                .nonOpaque() // Makes the block transparent
                .luminance(state -> state.get(ACTIVE) ? 7 : 0)); // Light up when active
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HoneyChargeConduitBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.HONEY_CHARGE_CONDUIT_BLOCK_ENTITY,
                (world1, pos, state1, be) -> HoneyChargeConduitBlockEntity.tick(world1, pos, state1, be));
    }
}