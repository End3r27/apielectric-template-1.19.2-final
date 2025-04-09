package end3r.apielectric.block;

import end3r.apielectric.block.entity.HoneyPowerConsumerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HoneyPowerConsumerBlock extends Block {
    public HoneyPowerConsumerBlock(Settings settings) {
        super(settings);
    }


    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HoneyPowerConsumerBlockEntity(pos, state);
    }



    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        // Check if the item is placed in the world
        if (world != null && stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = world.getBlockState(stack.getFrame().getBlockPos());
            if (state.getBlock() instanceof HoneyPowerConsumerBlock) {
                BlockEntity blockEntity = world.getBlockEntity(stack.getFrame().getBlockPos());
                if (blockEntity instanceof HoneyPowerConsumerBlockEntity honeyPowerConsumer) {
                    int charge = honeyPowerConsumer.getHoneyCharge();  // Retrieve honey charge from block entity
                    tooltip.add(Text.literal("Honey Charge: " + charge));
                }
            }
        } else {
            tooltip.add(Text.literal("Honey Charge: Requires placement"));
        }
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof HoneyPowerConsumerBlockEntity blockEntity) {
                blockEntity.openScreen(player);
            }
        }
        return ActionResult.SUCCESS;
    }



}
