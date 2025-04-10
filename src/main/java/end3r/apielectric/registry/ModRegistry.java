package end3r.apielectric.registry;


import end3r.apielectric.client.render.ModEntityRenderers;

public class ModRegistry {

    public static void registerAll() {
        ModBlocks.registerBlocks();
        ModEntities.register();
        ModItems.registerItems();
        ModBlockEntities.registerBlockEntities();
        ModEntityRenderers.registerEntityRenderers();
    }
    
    public static void registerClient() {
        // Client-specific registrations

    }
}
