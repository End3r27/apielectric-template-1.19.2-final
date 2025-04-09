package end3r.apielectric.registry;

import end3r.apielectric.item.HoneyChargeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    // Creative tab (item group)
    public static final ItemGroup APIELECTRIC_TAB = ModItemGroups.APIELECTRIC_GROUP;

    // Items
    public static final Item HONEY_POWER_CONSUMER_ITEM = registerItem(
            "honey_power_consumer",
            new BlockItem(ModBlocks.HONEY_POWER_CONSUMER_BLOCK, new FabricItemSettings().group(APIELECTRIC_TAB))
    );

    public static final Item HONEY_CHARGE_ITEM = registerItem("honey_charge_item", new HoneyChargeItem(1000, new Item.Settings()));

    // Registering method
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier("apielectric", name), item);
    }

    public static void registerItems() {
        System.out.println("Registering ModItems for apielectric");
    }
}
