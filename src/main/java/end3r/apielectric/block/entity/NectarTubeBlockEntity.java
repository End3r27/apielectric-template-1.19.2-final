package end3r.apielectric.block.entity;


import end3r.apielectric.energy.HoneyChargeProvider;
import end3r.apielectric.energy.HoneyChargeReceiver;
import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.BlockState;

public class NectarTubeBlockEntity extends BlockEntity {

    public NectarTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NECTAR_TUBE_BLOCK_ENTITY, pos, state);
    }

    public void transferEnergy(World world, BlockPos pos) {
        BlockPos[] neighbors = new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };

        for (BlockPos neighbor : neighbors) {
            BlockState neighborState = world.getBlockState(neighbor);
            if (neighborState.getBlock() instanceof HoneyChargeProvider provider) {
                int energyToTransfer = provider.provideHoneyCharge();
                for (BlockPos adjNeighbor : neighbors) {
                    BlockState adjNeighborState = world.getBlockState(adjNeighbor);
                    if (adjNeighborState.getBlock() instanceof HoneyChargeReceiver receiver) {
                        receiver.receiveHoneyCharge(energyToTransfer);
                    }
                }
            }
        }
    }
}
