package end3r.apielectric.block.entity;

import end3r.apielectric.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PollenTransducerBlockEntity extends BaseHoneyChargeBlockEntity {

    public static final int MAX_HONEYCHARGE = 5000;

    // Energy conversion settings
    private static final int FE_MODE = 0;
    private static final int RF_MODE = 1;
    private static final int AE_MODE = 2;
    private static final String[] MODE_NAMES = {"Forge Energy (FE)", "Redstone Flux (RF)", "Applied Energistics (AE)"};

    private int outputMode = FE_MODE; // Default to FE
    private int conversionRate = 10; // 1 HC = 10 FE/RF by default
    private int aeConversionRate = 2; // 1 HC = 2 AE by default (AE energy is more valuable)
    private int bufferEnergy = 0;
    private double bufferAEEnergy = 0.0;
    private final int maxBufferEnergy = 10000;
    private final double maxBufferAEEnergy = 1000.0;

    public PollenTransducerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POLLEN_TRANS_ENTITY, pos, state, MAX_HONEYCHARGE);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        // Convert HoneyCharge to the selected energy type
        if (getStoredHoneyCharge() > 0) {
            if (outputMode == AE_MODE) {
                convertToAE();
            } else {
                convertToFEorRF();
            }
        }

        // Export energy to adjacent blocks
        exportEnergy(world, pos);

        markDirty();
    }

    private void convertToFEorRF() {
        // Calculate how much energy can be added to the buffer
        int remainingCapacity = maxBufferEnergy - bufferEnergy;
        if (remainingCapacity > 0) {
            // Convert 1 HC at a time
            int honeyToConvert = Math.min(getStoredHoneyCharge(), 1);
            int energyToAdd = honeyToConvert * conversionRate;

            // Ensure we don't exceed capacity
            energyToAdd = Math.min(energyToAdd, remainingCapacity);

            if (energyToAdd > 0) {
                // Add to our buffer
                bufferEnergy += energyToAdd;
                setStoredHoneyCharge(getStoredHoneyCharge() - honeyToConvert);
            }
        }
    }

    private void convertToAE() {
        // Calculate how much AE energy can be added to the buffer
        double remainingCapacity = maxBufferAEEnergy - bufferAEEnergy;
        if (remainingCapacity > 0) {
            // Convert 1 HC at a time
            int honeyToConvert = Math.min(getStoredHoneyCharge(), 1);
            double energyToAdd = honeyToConvert * aeConversionRate;

            // Ensure we don't exceed capacity
            energyToAdd = Math.min(energyToAdd, remainingCapacity);

            if (energyToAdd > 0) {
                // Add to our buffer
                bufferAEEnergy += energyToAdd;
                setStoredHoneyCharge(getStoredHoneyCharge() - honeyToConvert);
            }
        }
    }

    private void exportEnergy(World world, BlockPos pos) {
        if ((outputMode != AE_MODE && bufferEnergy <= 0) ||
                (outputMode == AE_MODE && bufferAEEnergy <= 0)) {
            return;
        }

        // Find compatible energy-receiving blocks in adjacent positions
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.offset(direction);
            BlockEntity targetEntity = world.getBlockEntity(targetPos);

            if (targetEntity == null) continue;

            // Check if target can receive energy based on the current output mode
            boolean energyTransferred = false;

            switch (outputMode) {
                case FE_MODE:
                    // Forge Energy implementation
                    if (isForgeEnergyReceiver(targetEntity)) {
                        int transferred = transferForgeEnergy(targetEntity, direction.getOpposite());
                        bufferEnergy -= transferred;
                        energyTransferred = transferred > 0;
                    }
                    break;

                case RF_MODE:
                    // Redstone Flux implementation
                    if (isRedstoneFluxReceiver(targetEntity)) {
                        int transferred = transferRedstoneFlux(targetEntity, direction.getOpposite());
                        bufferEnergy -= transferred;
                        energyTransferred = transferred > 0;
                    }
                    break;

                case AE_MODE:
                    // AE2 implementation
                    if (isAppliedEnergisticsReceiver(targetEntity)) {
                        double transferred = transferAEEnergy(targetEntity, direction.getOpposite());
                        bufferAEEnergy -= transferred;
                        energyTransferred = transferred > 0;
                    }
                    break;
            }

            // If we transferred energy, we're done for this tick
            if (energyTransferred) {
                break;
            }
        }
    }

    // Energy transfer implementation methods

    private int transferForgeEnergy(BlockEntity entity, Direction from) {
        // This would use Forge Energy API
        // For example with Energy API:
        // EnergyStorage storage = EnergyStorage.SIDED.find(entity.getWorld(), entity.getPos(), from);
        // if (storage != null) {
        //     try (Transaction transaction = Transaction.openOuter()) {
        //         long inserted = storage.insert(bufferEnergy, transaction);
        //         transaction.commit();
        //         return (int) inserted;
        //     }
        // }

        // Placeholder implementation
        return Math.min(bufferEnergy, 100);
    }

    private int transferRedstoneFlux(BlockEntity entity, Direction from) {
        // This would use RF API
        // Similar to Forge Energy in many implementations

        // Placeholder implementation
        return Math.min(bufferEnergy, 100);
    }

    private double transferAEEnergy(BlockEntity entity, Direction from) {
        // This would use AE2 API
        // For example with AE2:
        // if (entity instanceof IPowerStorage powerStorage) {
        //     double injected = powerStorage.injectAEPower(Math.min(bufferAEEnergy, 10.0), from);
        //     return Math.min(bufferAEEnergy, 10.0) - injected; // Returns what was accepted
        // }

        // Placeholder implementation - AE2 typically works with smaller values
        return Math.min(bufferAEEnergy, 2.0);
    }

    // Compatibility check methods

    private boolean isForgeEnergyReceiver(BlockEntity entity) {
        // Check if entity can receive Forge Energy
        // return entity.getCapability(ForgeCapabilities.ENERGY, null).isPresent();
        return false; // Placeholder
    }

    private boolean isRedstoneFluxReceiver(BlockEntity entity) {
        // Check if entity can receive RF
        // return entity instanceof IEnergyReceiver;
        return false; // Placeholder
    }

    private boolean isAppliedEnergisticsReceiver(BlockEntity entity) {
        // Check if entity can receive AE energy
        // For AE2, this would typically check:
        // return entity instanceof IPowerStorage || entity instanceof IEnergyGrid;
        return false; // Placeholder
    }

    public void cycleOutputMode() {
        outputMode = (outputMode + 1) % MODE_NAMES.length;
        markDirty();
    }

    public Text getOutputModeText() {
        return Text.of("Output Mode: " + MODE_NAMES[outputMode]);
    }

    public int getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(int mode) {
        if (mode >= 0 && mode < MODE_NAMES.length) {
            this.outputMode = mode;
            markDirty();
        }
    }

    public int getConversionRate() {
        return outputMode == AE_MODE ? aeConversionRate : conversionRate;
    }

    public void setConversionRate(int rate) {
        if (rate > 0) {
            if (outputMode == AE_MODE) {
                this.aeConversionRate = rate;
            } else {
                this.conversionRate = rate;
            }
            markDirty();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        outputMode = nbt.getInt("OutputMode");
        conversionRate = nbt.getInt("ConversionRate");
        aeConversionRate = nbt.getInt("AEConversionRate");
        bufferEnergy = nbt.getInt("BufferEnergy");
        bufferAEEnergy = nbt.getDouble("BufferAEEnergy");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("OutputMode", outputMode);
        nbt.putInt("ConversionRate", conversionRate);
        nbt.putInt("AEConversionRate", aeConversionRate);
        nbt.putInt("BufferEnergy", bufferEnergy);
        nbt.putDouble("BufferAEEnergy", bufferAEEnergy);
    }
}