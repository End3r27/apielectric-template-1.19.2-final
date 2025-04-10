package end3r.apielectric.registry;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockTags {
    public static final TagKey<Block> ENERGIZED_FLOWERS = TagKey.of(Registry.BLOCK_KEY, new Identifier("apielectric", "energized_flowers"));
}
