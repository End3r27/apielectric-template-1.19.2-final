package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.block.CombCapacitorBlock;
import end3r.apielectric.block.EnergyApiaryBlock;
import end3r.apielectric.block.PollenTransducerBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    // Declare the blocks and provide Block.Settings
    public static final Block ENERGY_APIARY = new EnergyApiaryBlock(Block.Settings.of(net.minecraft.block.Material.WOOD));
    public static final Block COMB_CAPACITOR = new CombCapacitorBlock(Block.Settings.of(net.minecraft.block.Material.METAL));
    public static final Block POLLEN_TRANSDUCER = new PollenTransducerBlock(Block.Settings.of(net.minecraft.block.Material.STONE));

    public static void registerBlocks() {
        // Register blocks
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "energy_apiary"), ENERGY_APIARY);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "comb_capacitor"), COMB_CAPACITOR);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "pollen_transducer"), POLLEN_TRANSDUCER);

        // Register block items (used in inventory)
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "energy_apiary"), new BlockItem(ENERGY_APIARY, new net.minecraft.item.Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "comb_capacitor"), new BlockItem(COMB_CAPACITOR, new net.minecraft.item.Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "pollen_transducer"), new BlockItem(POLLEN_TRANSDUCER, new net.minecraft.item.Item.Settings()));
    }
}
