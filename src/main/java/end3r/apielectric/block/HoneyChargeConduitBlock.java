package end3r.apielectric.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class HoneyChargeConduitBlock extends Block {
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
}