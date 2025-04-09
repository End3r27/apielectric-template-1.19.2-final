package end3r.apielectric.registry;


public class ModRegistry {

    public static void registerAll() {
        ModBlocks.registerBlocks();
        ModEntities.registerEntities();
        ModItems.registerItems();
        ModBlockEntities.registerBlockEntities();
    }
}
