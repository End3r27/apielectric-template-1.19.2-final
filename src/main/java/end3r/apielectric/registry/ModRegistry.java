package end3r.apielectric.registry;

public class ModRegistry {

    public static void registerAll() {
        ModBlocks.registerBlocks();
        ModEntities.register();
        ModItems.registerItems();
        ModBlockEntities.registerBlockEntities();
        ModBlockTags.register();
    }

    public static void registerClient() {
        // Client-specific registrations
    }
}
