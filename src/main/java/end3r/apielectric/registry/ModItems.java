package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    // Declare custom items
    public static final Item HONEY_JAR = new Item(new Item.Settings());

    public static void registerItems() {
        // Register items
        Registry.register(Registry.ITEM, new Identifier(ApiElectric.MOD_ID, "honey_jar"), HONEY_JAR);
    }
}