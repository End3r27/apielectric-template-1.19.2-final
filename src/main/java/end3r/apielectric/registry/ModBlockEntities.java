package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {

    // Declare block entities
    public static final BlockEntityType<EnergyApiaryBlockEntity> ENERGY_APIARY_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(ApiElectric.MOD_ID, "energy_apiary"),
            BlockEntityType.Builder.create(EnergyApiaryBlockEntity::new, ModBlocks.ENERGY_APIARY).build(null)
    );

    public static final BlockEntityType<CombCapacitorBlockEntity> COMB_CAPACITOR_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(ApiElectric.MOD_ID, "comb_capacitor"),
            BlockEntityType.Builder.create(CombCapacitorBlockEntity::new, ModBlocks.COMB_CAPACITOR).build(null)
    );


    public static final BlockEntityType<BaseHoneyChargeBlockEntity> BASE_HONEY_CHARGE_BLOCK_ENTITY =
            Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    new Identifier(ApiElectric.MOD_ID, "base_honey_charge_block"),
                    BlockEntityType.Builder.create(BaseHoneyChargeBlockEntity::new, ModBlocks.BASE_HONEY_CHARGE_BLOCK).build(null)
            );

    public static final BlockEntityType<HoneyChargeConduitBlockEntity> HONEY_CHARGE_CONDUIT_BLOCK_ENTITY =
            Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    new Identifier(ApiElectric.MOD_ID, "honey_charge_conduit"),
                    FabricBlockEntityTypeBuilder.create(
                            HoneyChargeConduitBlockEntity::new,
                            ModBlocks.HONEY_CHARGE_CONDUIT).build()
            );

    public static final BlockEntityType<HoneyChargeFurnaceBlockEntity> HONEY_CHARGE_FURNACE_BLOCK_ENTITY =
            Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    new Identifier(ApiElectric.MOD_ID, "honey_charge_furnace"),
                    FabricBlockEntityTypeBuilder.create(
                            HoneyChargeFurnaceBlockEntity::new,
                            ModBlocks.HONEY_CHARGE_FURNACE).build()
            );

    // New Energy Bee Spawner Block Entity
    public static final BlockEntityType<EnergyBeeSpawnerBlockEntity> ENERGY_BEE_SPAWNER_ENTITY =
            Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    new Identifier(ApiElectric.MOD_ID, "energy_bee_spawner"),
                    FabricBlockEntityTypeBuilder.create(
                            EnergyBeeSpawnerBlockEntity::new,
                            ModBlocks.ENERGY_BEE_SPAWNER).build()
            );

    public static void registerBlockEntities() {
        // Method kept for explicit initialization if needed
        ApiElectric.LOGGER.info("Registering Block Entities for " + ApiElectric.MOD_ID);
    }
}