package end3r.apielectric.block;

import end3r.apielectric.bee.EnergyBeeEntity;
import end3r.apielectric.block.entity.EnergyBeeSpawnerBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyBeeSpawnerBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public EnergyBeeSpawnerBlock(Settings settings) {
        super(settings);
        // Set default state with facing direction
        setDefaultState(getStateManager().getDefaultState().with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Set facing direction based on player placement
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    // Make this block use a BlockEntity
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyBeeSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ENERGY_BEE_SPAWNER_ENTITY, EnergyBeeSpawnerBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EnergyBeeSpawnerBlockEntity) {
                EnergyBeeSpawnerBlockEntity spawner = (EnergyBeeSpawnerBlockEntity) blockEntity;

                // Get the current bee count in the area
                int currentBeeCount = spawner.countNearbyBees();

                // Provide feedback to the player
                player.sendMessage(net.minecraft.text.Text.literal("Energy Bees in range: " + currentBeeCount + "/10"), true);

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.CONSUME;
    }
}