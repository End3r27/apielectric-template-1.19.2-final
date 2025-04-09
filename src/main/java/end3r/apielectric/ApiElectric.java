package end3r.apielectric;

import end3r.apielectric.registry.ModBlockEntities;
import end3r.apielectric.registry.ModBlocks;
import end3r.apielectric.registry.ModItems;
import end3r.apielectric.screen.ModScreens;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiElectric implements ModInitializer {
	public static final String MOD_ID = "apielectric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Register blocks, items, block entities, and screens
		ModBlocks.registerBlocks();
		ModItems.registerItems();  // Register items before item group
		ModBlockEntities.registerBlockEntities();
		ModScreens.registerAll();


		LOGGER.info("Hello Fabric world!");
	}
}
