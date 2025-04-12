package end3r.apielectric.block;

import end3r.apielectric.block.entity.PollenTransducerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import end3r.apielectric.registry.ModBlockEntities;

public class PollenTransducerBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public PollenTransducerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PollenTransducerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.POLLEN_TRANS_ENTITY,
                (world1, pos, state1, be) -> ((PollenTransducerBlockEntity) be).tick(world1, pos, state1));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PollenTransducerBlockEntity transducer) {
                ItemStack stack = new ItemStack(this);
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("HoneyCharge", transducer.getStoredHoneyCharge());
                nbt.putInt("OutputMode", transducer.getOutputMode());
                nbt.putInt("ConversionRate", transducer.getConversionRate());
                stack.setNbt(nbt);
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, net.minecraft.entity.LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);

        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof PollenTransducerBlockEntity transducer) {
                if (nbt.contains("HoneyCharge")) {
                    transducer.setStoredHoneyCharge(nbt.getInt("HoneyCharge"));
                }
                if (nbt.contains("OutputMode")) {
                    transducer.setOutputMode(nbt.getInt("OutputMode"));
                }
                if (nbt.contains("ConversionRate")) {
                    transducer.setConversionRate(nbt.getInt("ConversionRate"));
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PollenTransducerBlockEntity transducer) {
                // Cycle through energy output modes when the player right-clicks
                transducer.cycleOutputMode();
                player.sendMessage(transducer.getOutputModeText(), false);

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.CONSUME;
    }
}