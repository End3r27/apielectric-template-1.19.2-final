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
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    // Input slot, output slot, and optional upgrade slot
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int UPGRADE_SLOT = 2;

    // Energy storage
    private int honeyCharge = 0;
    private final int maxHoneyCharge = 10000;
    private final int honeyChargePerOperation = 50;

    // Cooking properties
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private int honeyChargeBurnTime = 0;

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

    public void addHoneyCharge(int amount) {
        this.honeyCharge = Math.min(honeyCharge + amount, maxHoneyCharge);
    }

    public int getHoneyCharge() {
        return honeyCharge;
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
     * Implementation of HoneyChargeReceiver interface
     * @param amount The amount of honey charge to receive
     */
    @Override
    public void receiveHoneyCharge(int amount) {
        if (amount <= 0) return;

        // Limit to available space
        int spaceAvailable = maxHoneyCharge - honeyCharge;
        int actualAmount = Math.min(amount, spaceAvailable);

        if (actualAmount > 0) {
            honeyCharge += actualAmount;
            markDirty();

            // Visual feedback when receiving charge
            if (world != null && !world.isClient) {
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
                        3, // number of particles
                        0.2, 0.1, 0.2, // spread
                        0.01 // speed
                );
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, HoneyChargeFurnaceBlockEntity entity) {
        if (world.isClient()) return;

        boolean wasActive = entity.honeyChargeBurnTime > 0;
        boolean changed = false;

        // If there's HoneyCharge, convert it to burn time
        if (entity.honeyCharge >= entity.honeyChargePerOperation && entity.honeyChargeBurnTime <= 0) {
            entity.honeyCharge -= entity.honeyChargePerOperation;
            entity.honeyChargeBurnTime = 40; // Each charge burst lasts for 40 ticks
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