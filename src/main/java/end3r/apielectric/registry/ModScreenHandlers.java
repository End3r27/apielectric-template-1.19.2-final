package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.screen.HoneyChargeFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModScreenHandlers {
    public static final ScreenHandlerType<HoneyChargeFurnaceScreenHandler> HONEY_CHARGE_FURNACE_SCREEN_HANDLER =
            Registry.register(
                    Registry.SCREEN_HANDLER,
                    new Identifier(ApiElectric.MOD_ID, "honey_charge_furnace"),
                    new ScreenHandlerType<>(HoneyChargeFurnaceScreenHandler::new)
            );

    public static void registerAllScreenHandlers() {
        ApiElectric.LOGGER.info("Registering screen handlers for " + ApiElectric.MOD_ID);
    }
}