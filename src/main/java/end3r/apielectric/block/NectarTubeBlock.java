package end3r.apielectric.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class NectarTubeBlock extends Block {
    public NectarTubeBlock() {
        super(Settings.of(Material.GLASS).strength(1.0F, 1.0F));
    }
}
