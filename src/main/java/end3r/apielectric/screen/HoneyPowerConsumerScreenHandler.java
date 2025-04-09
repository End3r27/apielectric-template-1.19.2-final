package end3r.apielectric.screen;

import end3r.apielectric.block.entity.HoneyPowerConsumerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class HoneyPowerConsumerScreenHandler extends ScreenHandler {
    private final HoneyPowerConsumerBlockEntity blockEntity;

    public HoneyPowerConsumerScreenHandler(int syncId, PlayerInventory playerInventory, HoneyPowerConsumerBlockEntity blockEntity) {
        super(ModScreens.HONEY_POWER_CONSUMER_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;

        // Add player inventory slots (optional)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public HoneyPowerConsumerBlockEntity getBlockEntity() {
        return blockEntity;
    }
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

}
