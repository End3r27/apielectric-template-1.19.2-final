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
    public static final Item HONEY_JAR = new Item(new Item.Settings());
    public static final Item ENERGY_BEE_SPAWN_EGG = new SpawnEggItem(
            ModEntities.ENERGY_BEE, 0xfcd734, 0x463b1e,
            new Item.Settings());

    // 🟡 Custom Creative Tab (Move this after the items are registered)
    public static ItemGroup APIELECTRIC_GROUP;

    public static void registerItems() {
        // Register items
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "honey_jar"), HONEY_JAR);
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "energy_bee_spawn_egg"), ENERGY_BEE_SPAWN_EGG);

        // 🟡 Now initialize the creative tab after registration
        APIELECTRIC_GROUP = FabricItemGroupBuilder.create(
                        new Identifier("apielectric", "group"))
                .icon(() -> new ItemStack(ENERGY_BEE_SPAWN_EGG))
                .appendItems(stacks -> {
                    stacks.add(new ItemStack(HONEY_JAR));
                    stacks.add(new ItemStack(ENERGY_BEE_SPAWN_EGG));
                    stacks.add(new ItemStack(ModBlocks.BASE_HONEY_CHARGE_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.COMB_CAPACITOR));
                    stacks.add(new ItemStack(ModBlocks.ENERGY_APIARY));
                    stacks.add(new ItemStack(ModBlocks.POLLEN_TRANSDUCER));
                    // Add more items here if needed
                })
                .build();
    }

}
