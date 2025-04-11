package end3r.apielectric;

import end3r.apielectric.block.NectarTubeBlock;
import end3r.apielectric.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiElectric implements ModInitializer {
	public static final String MOD_ID = "apielectric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Register blocks and other mod components
		ModRegistry.registerAll();

		// Register Nectar Tube Block model and texture rendering
		registerModels();

		// Print initialization log message
		LOGGER.info("ApiElectric mod initialized!");
	}

	private void registerModels() {
		// Corrected: passing the NectarTubeBlock class reference and RenderLayer
		BlockRenderLayerMap.INSTANCE.putBlock(NectarTubeBlock, RenderLayer.getTranslucent());
	}
}
