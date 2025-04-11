package end3r.apielectric;

import end3r.apielectric.block.entity.BaseHoneyChargeBlockEntity;
import end3r.apielectric.registry.ModBlockEntities;
import end3r.apielectric.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
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
	public static final BlockEntityType<BaseHoneyChargeBlockEntity> BASE_HONEY_CHARGE_BLOCK_ENTITY =
			ModBlockEntities.BASE_HONEY_CHARGE_BLOCK_ENTITY;
}

