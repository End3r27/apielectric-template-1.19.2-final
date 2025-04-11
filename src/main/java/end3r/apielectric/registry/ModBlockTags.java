package end3r.apielectric.registry;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockTags {

    public static final TagKey<Block> ENERGIZED_FLOWERS = TagKey.of(Registry.BLOCK_KEY, new Identifier("apielectric", "energized_flowers"));

    // Registering the tag
    public static void register() {
        // For now, there is no explicit registration method for Tags in Fabric API.
        // The tags are registered by other mods or processes that load the data pack.

        // This is where you would normally register items or blocks to this tag.
        // Example: Registry.BLOCK.getOrCreateEntry(ENERGIZED_FLOWERS)
    }
}
