package end3r.apielectric.registry;

import end3r.apielectric.block.HoneyPowerConsumerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static final Block HONEY_POWER_CONSUMER_BLOCK = registerBlock("honey_power_consumer", new HoneyPowerConsumerBlock(AbstractBlock.Settings.of(Material.AMETHYST)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registry.REGISTRIES.BLOCK, new Identifier("apielectric", name), block);
    }

    public static void registerBlocks() {
        System.out.println("Registering ModBlocks for " + "apielectric");
    }
}
