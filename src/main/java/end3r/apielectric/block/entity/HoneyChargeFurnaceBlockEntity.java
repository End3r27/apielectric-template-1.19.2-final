package end3r.apielectric.block.entity;

import end3r.apielectric.energy.HoneyChargeReceiver;
import end3r.apielectric.registry.ModBlockEntities;
import end3r.apielectric.screen.HoneyChargeFurnaceScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import end3r.apielectric.block.HoneyChargeFurnaceBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class HoneyChargeFurnaceBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, HoneyChargeReceiver {
    // Changed to 2 slots: input and output only
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    // Updated slot indices
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    // Energy storage
    private int honeyCharge = 0;
    private final int maxHoneyCharge = 10000;
    private final int honeyChargePerOperation = 25;

    // Cooking properties
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private int honeyChargeBurnTime = 0;

    // A flag to check if we received energy this tick for visual effects
    private boolean receivedEnergy = false;
    private int energyReceivedThisTick = 0;

    public HoneyChargeFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HONEY_CHARGE_FURNACE_BLOCK_ENTITY, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> HoneyChargeFurnaceBlockEntity.this.progress;
                    case 1 -> HoneyChargeFurnaceBlockEntity.this.maxProgress;
                    case 2 -> HoneyChargeFurnaceBlockEntity.this.honeyCharge;
                    case 3 -> HoneyChargeFurnaceBlockEntity.this.maxHoneyCharge;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> HoneyChargeFurnaceBlockEntity.this.progress = value;
                    case 1 -> HoneyChargeFurnaceBlockEntity.this.maxProgress = value;
                    case 2 -> HoneyChargeFurnaceBlockEntity.this.honeyCharge = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    /**
     * Adds honey charge to the furnace
     * @param amount The amount to add
     */
    public void addHoneyCharge(int amount) {
        if (amount <= 0) return;

        int spaceAvailable = maxHoneyCharge - honeyCharge;
        int actualAmount = Math.min(amount, spaceAvailable);

        if (actualAmount > 0) {
            this.honeyCharge += actualAmount;
            this.energyReceivedThisTick += actualAmount;
            this.receivedEnergy = true;
            markDirty();
        }
    }

    /**
     * Gets the current honey charge
     * @return Current honey charge level
     */
    public int getHoneyCharge() {
        return honeyCharge;
    }

    /**
     * Gets the maximum honey charge this furnace can store
     * @return Maximum honey charge capacity
     */
    public int getMaxHoneyCharge() {
        return maxHoneyCharge;
    }

    /**
     * Sets the honey charge to the specified amount
     * @param amount The amount to set the honey charge to
     */
    public void setHoneyCharge(int amount) {
        this.honeyCharge = Math.max(0, Math.min(amount, maxHoneyCharge));
        markDirty();
    }

    /**
     * Writes inventory data to an NBT compound
     * @param nbt The NBT compound to write to
     */
    public void writeInventory(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
    }

    /**
     * Reads inventory data from an NBT compound
     * @param nbt The NBT compound to read from
     */
    public void readInventory(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.apielectric.honey_charge_furnace");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HoneyChargeFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("honey_charge_furnace.progress", progress);
        nbt.putInt("honey_charge_furnace.honeyCharge", honeyCharge);
        nbt.putInt("honey_charge_furnace.honeyChargeBurnTime", honeyChargeBurnTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("honey_charge_furnace.progress");
        honeyCharge = nbt.getInt("honey_charge_furnace.honeyCharge");
        honeyChargeBurnTime = nbt.getInt("honey_charge_furnace.honeyChargeBurnTime");
    }

    /**
     * Implementation of HoneyChargeReceiver interface - accepts honey charge from any source
     *
     * @param amount The amount of honey charge to receive
     * @return
     */
    @Override
    public int receiveHoneyCharge(int amount) {
        addHoneyCharge(amount);
        return amount;
    }

    /**
     * Displays visual effects for receiving honey charge
     */
    private void showEnergyReceivedEffects() {
        if (world != null && !world.isClient && receivedEnergy && energyReceivedThisTick > 0) {
            // Adjust particle count based on amount received
            int particleCount = Math.min(5, 1 + (energyReceivedThisTick / 50));

            world.playSound(
                    null,
                    pos,
                    SoundEvents.BLOCK_HONEY_BLOCK_PLACE,
                    SoundCategory.BLOCKS,
                    0.7f,
                    1.2f
            );

            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    particleCount, // number of particles based on energy received
                    0.2, 0.1, 0.2, // spread
                    0.01 // speed
            );

            // Reset flags for next tick
            receivedEnergy = false;
            energyReceivedThisTick = 0;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, HoneyChargeFurnaceBlockEntity entity) {
        if (world.isClient()) return;

        boolean wasActive = entity.honeyChargeBurnTime > 0;
        boolean changed = false;

        // Show visual effects if we received energy
        entity.showEnergyReceivedEffects();

        // Start a new smelting operation if we have enough charge
        if (canSmelt(entity) && entity.honeyCharge >= entity.honeyChargePerOperation && entity.honeyChargeBurnTime <= 0) {
            entity.honeyCharge -= entity.honeyChargePerOperation;
            entity.honeyChargeBurnTime = 40;
            changed = true;
        }

        // Process smelting if we have burn time
        if (entity.honeyChargeBurnTime > 0) {
            entity.honeyChargeBurnTime--;

            // Check if we can smelt
            if (canSmelt(entity)) {
                entity.progress++;
                if (entity.progress >= entity.maxProgress) {
                    entity.progress = 0;
                    smeltItem(entity);
                }
            } else {
                entity.progress = 0;
            }

            changed = true;
        } else if (entity.progress > 0) {
            // If we ran out of burn time, start reducing progress
            entity.progress = Math.max(0, entity.progress - 2);
            changed = true;
        }

        // Update block state if activity changed
        boolean isActive = entity.honeyChargeBurnTime > 0;
        if (wasActive != isActive) {
            world.setBlockState(pos, state.with(HoneyChargeFurnaceBlock.LIT, isActive));
            changed = true;
        }

        if (changed) {
            markDirty(world, pos, state);
        }
    }

    private static boolean canSmelt(HoneyChargeFurnaceBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null) return false;

        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
        for (int i = 0; i < entity.inventory.size(); i++) {
            inventory.setStack(i, entity.getStack(i));
        }

        // Check if we have a valid recipe
        Optional<SmeltingRecipe> recipe = world.getRecipeManager()
                .getFirstMatch(RecipeType.SMELTING, inventory, world);

        // Check if we can output the result
        if (recipe.isPresent()) {
            ItemStack output = recipe.get().getOutput();
            ItemStack currentOutput = entity.inventory.get(OUTPUT_SLOT);

            if (currentOutput.isEmpty()) {
                return true;
            }

            if (!currentOutput.isItemEqual(output)) {
                return false;
            }

            int result = currentOutput.getCount() + output.getCount();
            return result <= entity.getMaxCountPerStack() && result <= currentOutput.getMaxCount();
        }

        return false;
    }

    private static void smeltItem(HoneyChargeFurnaceBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null) return;

        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
        for (int i = 0; i < entity.inventory.size(); i++) {
            inventory.setStack(i, entity.getStack(i));
        }

        Optional<SmeltingRecipe> recipe = world.getRecipeManager()
                .getFirstMatch(RecipeType.SMELTING, inventory, world);

        if (recipe.isPresent()) {
            // Extract input
            entity.removeStack(INPUT_SLOT, 1);

            // Insert output
            ItemStack output = recipe.get().getOutput();
            ItemStack currentOutput = entity.inventory.get(OUTPUT_SLOT);

            if (currentOutput.isEmpty()) {
                entity.inventory.set(OUTPUT_SLOT, output.copy());
            } else if (currentOutput.isItemEqual(output)) {
                currentOutput.increment(output.getCount());
            }
        }
    }
}