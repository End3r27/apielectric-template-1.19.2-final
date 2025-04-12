package end3r.apielectric.block;

import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class EnergyApiaryBlock extends BlockWithEntity {

    public EnergyApiaryBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyApiaryBlockEntity(pos, state);  // Return a new instance of EnergyApiaryBlockEntity
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ENERGY_APIARY_ENTITY,
                (tickWorld, pos, tickState, blockEntity) -> EnergyApiaryBlockEntity.tick(tickWorld, pos, tickState, blockEntity));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EnergyApiaryBlockEntity apiary) {
                ItemStack stack = new ItemStack(this);
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("HoneyCharge", apiary.getStoredHoneyCharge());
                stack.setNbt(nbt);
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
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



    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnergyApiaryBlockEntity apiary) {
            ItemStack stack = new ItemStack(this);
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("HoneyCharge", apiary.getStoredHoneyCharge());
            stack.setNbt(nbt);
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }

        super.onBreak(world, pos, state, player);  // Call the superclass method to handle normal breaking
    }
}
