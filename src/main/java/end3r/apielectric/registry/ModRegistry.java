package end3r.apielectric.registry;


public class ModRegistry {

    public static void registerAll() {
        ModBlocks.registerBlocks();
        ModEntities.registerEntities();
        ModItems.registerItems();
        ModBlockEntities.registerBlockEntities();
    }
    
    public static void registerClient() {
        // Client-specific registrations
        ModelBees modelBees = new ModelBees();
        modelBees.onInitializeClient();
    }
}
