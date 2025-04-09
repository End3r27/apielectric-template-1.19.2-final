package end3r.apielectric;

import end3r.apielectric.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiElectric implements ModInitializer {
	public static final String MOD_ID = "apielectric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModRegistry.registerAll();

		LOGGER.info("Hello from ApiElectric!");
	}
}
