package end3r.apielectric.block;

import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import end3r.apielectric.block.entity.HoneyChargeFurnaceBlockEntity;
import end3r.apielectric.energy.HoneyChargeReceiver;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HoneyChargeFurnaceBlock extends BlockWithEntity implements HoneyChargeReceiver {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public HoneyChargeFurnaceBlock(Settings settings) {
        super(Settings.of(Material.STONE)
                .requiresTool()
                .strength(3.5F)
                .luminance(state -> state.get(LIT) ? 13 : 0));
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HoneyChargeFurnaceBlockEntity(pos, state);
    }



    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.HONEY_CHARGE_FURNACE_BLOCK_ENTITY,
                (world1, pos, state1, be) -> HoneyChargeFurnaceBlockEntity.tick(world1, pos, state1, be));
    }

    @Override
    public void receiveHoneyCharge(int amount) {
        // This method is required by the HoneyChargeReceiver interface
        // But the actual implementation is in the block entity
        // This method is never actually called - it's just here to satisfy the interface
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EnergyApiaryBlockEntity apiary) {
                ItemStack stack = new ItemStack(this);
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("HoneyCharge", apiary.getStoredHoneyCharge());
                stack.setNbt(nbt);
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, net.minecraft.entity.LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);

        if (stack.hasNbt() && stack.getNbt().contains("HoneyCharge")) {
            int storedCharge = stack.getNbt().getInt("HoneyCharge");

            // Get the block entity and set the charge
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EnergyApiaryBlockEntity apiaryEntity) {
                apiaryEntity.setStoredHoneyCharge(storedCharge);
            }
        }
    }
}