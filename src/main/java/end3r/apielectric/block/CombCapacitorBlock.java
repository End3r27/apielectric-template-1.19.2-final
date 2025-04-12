package end3r.apielectric.block;

import end3r.apielectric.block.entity.CombCapacitorBlockEntity;
import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CombCapacitorBlock extends BlockWithEntity implements BlockEntityProvider {

    public CombCapacitorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Return a new instance of CombCapacitorBlockEntity
        return new CombCapacitorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // This now works because checkType is inherited from BlockWithEntity
        return checkType(type, ModBlockEntities.COMB_CAPACITOR_ENTITY,
                (tickWorld, pos, tickState, blockEntity) -> CombCapacitorBlockEntity.tick(tickWorld, pos, tickState, blockEntity));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CombCapacitorBlockEntity apiary) {
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
            if (blockEntity instanceof CombCapacitorBlockEntity apiaryEntity) {
                apiaryEntity.setStoredHoneyCharge(storedCharge);
            }
        }
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // Use SOLID instead of the default MODEL to make the block visible
        return BlockRenderType.MODEL;
    }

}
