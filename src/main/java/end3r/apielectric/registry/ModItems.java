package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    // Declare custom items
    public static final Item HONEY_JAR = new Item(new Item.Settings().group(ModItems.APIELECTRIC_GROUP));
    public static final Item ENERGY_BEE_SPAWN_EGG = new SpawnEggItem(
            ModEntities.ENERGY_BEE, 0xfcd734, 0x463b1e,
            new Item.Settings().group(ModItems.APIELECTRIC_GROUP));

    // 🟡 Custom Creative Tab
    public static final ItemGroup APIELECTRIC_GROUP = FabricItemGroupBuilder.create(
                    new Identifier("apielectric", "group"))
            .icon(() -> new ItemStack(ModItems.ENERGY_BEE_SPAWN_EGG))
            .build();



    public static void registerItems() {
        // Register items
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "honey_jar"), HONEY_JAR);
    }
}