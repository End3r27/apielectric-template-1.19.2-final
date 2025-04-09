package end3r.apielectric.screen;

import end3r.apielectric.block.entity.HoneyPowerConsumerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;

public class ModScreens {
    public static final ScreenHandlerType<HoneyPowerConsumerScreenHandler> HONEY_POWER_CONSUMER_SCREEN_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    new Identifier("apielectric", "honey_power_consumer"),
                    (syncId, playerInventory, buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        BlockEntity blockEntity = playerInventory.player.world.getBlockEntity(pos);
                        if (blockEntity instanceof HoneyPowerConsumerBlockEntity powerConsumer) {
                            return new HoneyPowerConsumerScreenHandler(syncId, playerInventory, powerConsumer);
                        }
                        return null; // return null if no valid block entity is found
                    });

    public static void registerAll() {
        // No need for additional registration code
    }
}
