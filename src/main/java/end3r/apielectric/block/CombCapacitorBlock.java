package end3r.apielectric.block;

import end3r.apielectric.block.entity.CombCapacitorBlockEntity;
import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CombCapacitorBlock extends Block implements BlockEntityProvider {

    public CombCapacitorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Return a new instance of CombCapacitorBlockEntity
        return new CombCapacitorBlockEntity(pos, state);
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
