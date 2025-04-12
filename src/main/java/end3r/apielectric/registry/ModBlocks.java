package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.block.*;
import end3r.apielectric.item.TooltipBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    // Declare the blocks and provide Block.Settings
    public static final Block ENERGY_APIARY = new EnergyApiaryBlock(Block.Settings.of(net.minecraft.block.Material.WOOD));
    public static final Block COMB_CAPACITOR = new CombCapacitorBlock(Block.Settings.of(net.minecraft.block.Material.METAL));
    public static final Block POLLEN_TRANSDUCER = new PollenTransducerBlock(Block.Settings.of(net.minecraft.block.Material.STONE));
    public static final Block BASE_HONEY_CHARGE_BLOCK = new BaseHoneyChargeBlock(Block.Settings.of(net.minecraft.block.Material.WOOD));
    public static final Block ENERGIZED_FLOWER = new EnergizedFlowerBlock(FabricBlockSettings.copyOf(Blocks.DANDELION).nonOpaque().luminance(state -> 7)); // optional: emits light
    public static final Block HONEY_CHARGE_CONDUIT = new HoneyChargeConduitBlock(Block.Settings.of(Material.GLASS));
    public static final Block HONEY_CHARGE_FURNACE = new HoneyChargeFurnaceBlock((Block.Settings.of(net.minecraft.block.Material.METAL)));

    public static void registerBlocks() {
        // Register blocks
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "energy_apiary"), ENERGY_APIARY);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "comb_capacitor"), COMB_CAPACITOR);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "pollen_transducer"), POLLEN_TRANSDUCER);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "base_honey_charge_block"), BASE_HONEY_CHARGE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("apielectric", "energized_flower"), ENERGIZED_FLOWER);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "honey_charge_conduit"), HONEY_CHARGE_CONDUIT);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "honey_charge_furnace"), HONEY_CHARGE_FURNACE);

        // Register block items with tooltips
        registerBlockItem("energy_apiary", ENERGY_APIARY, "energy_apiary");
        registerBlockItem("comb_capacitor", COMB_CAPACITOR, "comb_capacitor");
        registerBlockItem("pollen_transducer", POLLEN_TRANSDUCER, "pollen_transducer");
        registerBlockItem("base_honey_charge_block", BASE_HONEY_CHARGE_BLOCK, "base_honey_charge_block");
        registerBlockItem("energized_flower", ENERGIZED_FLOWER, "energized_flower");
        registerBlockItem("honey_charge_conduit", HONEY_CHARGE_CONDUIT, "honey_charge_conduit");
        registerBlockItem("honey_charge_furnace", HONEY_CHARGE_FURNACE, "honey_charge_furnace");
    }

    /**
     * Helper method to register a block item with tooltips
     *
     * @param path The path/identifier for the block
     * @param block The block instance
     * @param tooltipKey The tooltip key suffix (without "tooltip.apielectric." prefix)
     */
    private static void registerBlockItem(String path, Block block, String tooltipKey) {
        Registry.register(
                Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, path),
                new TooltipBlockItem(block, tooltipKey, new Item.Settings().group(ModItems.APIELECTRIC_GROUP))
        );
    }

}