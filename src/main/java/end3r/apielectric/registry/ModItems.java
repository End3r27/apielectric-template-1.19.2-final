package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.item.ApiaryUpgradeItem;
import end3r.apielectric.item.TooltipItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vazkii.patchouli.api.PatchouliAPI;

public class ModItems {

    // Declare custom items
    public static final Item ENERGY_BEE_SPAWN_EGG = new SpawnEggItem(
            ModEntities.ENERGY_BEE,
            0xFFD700, // Primary color (gold)
            0xE6E6FA, // Secondary color (light purple)
            new Item.Settings().group(ModItems.APIELECTRIC_GROUP));

    public static final Item ENERGIZED_FRAME = new ApiaryUpgradeItem(
            new FabricItemSettings().group(ItemGroup.MISC).maxCount(1),
            ApiaryUpgradeItem.UpgradeType.ENERGIZED_FRAME,
            "Now with extra zing! ⚡"
    );

    public static final Item WAX_INFUSED_SHIELDING = new ApiaryUpgradeItem(
            new FabricItemSettings().group(ItemGroup.MISC).maxCount(1),
            ApiaryUpgradeItem.UpgradeType.WAX_SHIELDING,
            "For when creepers crash the garden party. 💥"
    );

    public static final Item APIARY_CAPACITOR = new ApiaryUpgradeItem(
            new FabricItemSettings().group(ItemGroup.MISC).maxCount(1),
            ApiaryUpgradeItem.UpgradeType.APIARY_CAPACITOR,
            "Store more buzz before the burn."
    );

            // 🟡 Custom Creative Tab (Move this after the items are registered)
    public static ItemGroup APIELECTRIC_GROUP;

    public static void registerItems() {
        // Register items
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "energy_bee_spawn_egg"), ENERGY_BEE_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "energized_frame"), ENERGIZED_FRAME);
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "wax_infused_shielding.json"), WAX_INFUSED_SHIELDING);
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "apiary_capacitor"), APIARY_CAPACITOR);

        // 🟡 Now initialize the creative tab after registration
        APIELECTRIC_GROUP = FabricItemGroupBuilder.create(
                        new Identifier("apielectric", "group"))
                .icon(() -> new ItemStack(ENERGY_BEE_SPAWN_EGG))
                .appendItems(stacks -> {
                    stacks.add(new ItemStack(ENERGY_BEE_SPAWN_EGG));
                    stacks.add(new ItemStack(ModBlocks.ENERGY_APIARY));
                    stacks.add(new ItemStack(ModBlocks.ENERGIZED_FLOWER));
                    stacks.add(new ItemStack(ModBlocks.HONEY_CHARGE_CONDUIT));
                    stacks.add(new ItemStack(ModBlocks.HONEY_CHARGE_FURNACE));
                    stacks.add(new ItemStack(ModBlocks.ENERGY_BEE_SPAWNER));
                    // stacks.add(new ItemStack(ModBlocks.COMB_CAPACITOR)); currently not working!




                    // Add the guidebook using our helper method
                    ItemStack guideBook = ModBooks.getGuideBookStack();
                    if (!guideBook.isEmpty()) {
                        stacks.add(guideBook);
                    }
                })
                .build();
    }
}