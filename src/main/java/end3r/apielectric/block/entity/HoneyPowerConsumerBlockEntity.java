package end3r.apielectric.block.entity;

import end3r.apielectric.energy.HoneyChargeStorage;
import end3r.apielectric.energy.IHoneyCharge;
import end3r.apielectric.registry.ModBlockEntities;
import end3r.apielectric.screen.HoneyPowerConsumerScreenHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoneyPowerConsumerBlockEntity extends BlockEntity implements IHoneyCharge {

    private HoneyChargeStorage honeyChargeStorage;

    public HoneyPowerConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HONEY_POWER_CONSUMER, pos, state);
        honeyChargeStorage = new HoneyChargeStorage(10000, true, true); // 10,000 capacity
    }

    @Override
    public int getHoneyCharge() {
        return honeyChargeStorage.getHoneyCharge();
    }

    @Override
    public int getMaxHoneyCharge() {
        return honeyChargeStorage.getMaxHoneyCharge();
    }

    @Override
    public boolean canExtract() {
        return honeyChargeStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return honeyChargeStorage.canReceive();
    }

    @Override
    public int addHoneyCharge(int amount) {
        return honeyChargeStorage.addHoneyCharge(amount);
    }

    @Override
    public int extractHoneyCharge(int amount) {
        return honeyChargeStorage.extractHoneyCharge(amount);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);  // Call the superclass' writeNbt method
        // Store HoneyCharge
        nbt.putInt("honeyCharge", honeyChargeStorage.getHoneyCharge());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);  // Call the superclass' readNbt method
        // Restore HoneyCharge
        honeyChargeStorage = new HoneyChargeStorage(10000, true, true);
        honeyChargeStorage.addHoneyCharge(nbt.getInt("honeyCharge"));
    }

    public void openScreen(PlayerEntity player) {
        if (!world.isClient()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(pos);
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, playerEntity) -> {
                return new HoneyPowerConsumerScreenHandler(syncId, inv, this);
            }, Text.literal("Honey Power Consumer")));
        }
    }



}


