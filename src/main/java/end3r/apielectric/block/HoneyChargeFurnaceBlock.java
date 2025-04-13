package end3r.apielectric.block;

import end3r.apielectric.block.entity.HoneyChargeFurnaceBlockEntity;
import end3r.apielectric.energy.HoneyChargeReceiver;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
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
        super(settings);
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

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof HoneyChargeFurnaceBlockEntity furnaceEntity) {
                // Create an item stack for the block
                ItemStack stack = new ItemStack(this);

                // Create NBT data to store the honey charge
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("HoneyCharge", furnaceEntity.getHoneyCharge());

                // Store any inventory contents as well
                NbtCompound inventory = new NbtCompound();
                furnaceEntity.writeInventory(inventory);
                if (!inventory.isEmpty()) {
                    nbt.put("Inventory", inventory);
                }

                // Only add NBT if we have data to store
                if (!nbt.isEmpty()) {
                    stack.setNbt(nbt);
                }

                // Drop the item in the world
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);

                // Also drop any items that might be in the inventory
                // (Alternative to storing them in NBT, depending on your preference)
                // ItemScatterer.spawn(world, pos, furnaceEntity.getInventory());

                // Update the world - corrected method
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.HONEY_CHARGE_FURNACE_BLOCK_ENTITY,
                (world1, pos, state1, be) -> HoneyChargeFurnaceBlockEntity.tick(world1, pos, state1, be));
    }

    @Override
    public int receiveHoneyCharge(int amount) {
        // This method is required by the HoneyChargeReceiver interface
        // But the actual implementation is in the block entity
        // This method is never actually called - it's just here to satisfy the interface
        return amount;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        // If the stack has NBT data, apply it to the block entity
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HoneyChargeFurnaceBlockEntity furnaceEntity && itemStack.hasNbt()) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt != null) {
                // Restore honey charge
                if (nbt.contains("HoneyCharge")) {
                    furnaceEntity.setHoneyCharge(nbt.getInt("HoneyCharge"));
                }

                // Restore inventory items if present
                if (nbt.contains("Inventory")) {
                    furnaceEntity.readInventory(nbt.getCompound("Inventory"));
                }
            }
        }
    }
}