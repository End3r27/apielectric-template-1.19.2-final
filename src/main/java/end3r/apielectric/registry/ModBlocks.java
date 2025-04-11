package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.block.*;
import end3r.apielectric.item.TooltipBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
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
    public static final Block NCTR_TUBE = new NectarTubeBlock();


    public static void registerBlocks() {
        // Register blocks
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "energy_apiary"), ENERGY_APIARY);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "comb_capacitor"), COMB_CAPACITOR);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "pollen_transducer"), POLLEN_TRANSDUCER);
        Registry.register(Registry.BLOCK, new Identifier(ApiElectric.MOD_ID, "base_honey_charge_block"), BASE_HONEY_CHARGE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("apielectric", "energized_flower"), ENERGIZED_FLOWER);
        Registry.register(Registry.BLOCK, new Identifier("apielectric", "nectar_tube"), NCTR_TUBE);


        // Register block items (used in inventory)
        Registry.register(Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, "energy_apiary"),
                new TooltipBlockItem(ENERGY_APIARY, "tooltip.apielectric.energy_apiary", new Item.Settings().group(ModItems.APIELECTRIC_GROUP)));

        Registry.register(Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, "comb_capacitor"),
                new TooltipBlockItem(COMB_CAPACITOR, "tooltip.apielectric.comb_capacitor", new Item.Settings().group(ModItems.APIELECTRIC_GROUP)));

        Registry.register(Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, "pollen_transducer"),
                new TooltipBlockItem(POLLEN_TRANSDUCER, "tooltip.apielectric.pollen_transducer", new Item.Settings().group(ModItems.APIELECTRIC_GROUP)));

        // Use TooltipBlockItem for the Base Honey Charge Block as well
        Registry.register(Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, "base_honey_charge_block"),
                new TooltipBlockItem(BASE_HONEY_CHARGE_BLOCK, "tooltip.apielectric.base_honey_charge_block", new Item.Settings().group(ModItems.APIELECTRIC_GROUP)));

        Registry.register(Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, "energized_flower"),
                new BlockItem(ENERGIZED_FLOWER, new Item.Settings().group(ModItems.APIELECTRIC_GROUP)));

        Registry.register(Registry.ITEM,
                new Identifier(ApiElectric.MOD_ID, "nectar_tube"),
                new BlockItem(NCTR_TUBE, new Item.Settings().group(ModItems.APIELECTRIC_GROUP)));
    }
}

