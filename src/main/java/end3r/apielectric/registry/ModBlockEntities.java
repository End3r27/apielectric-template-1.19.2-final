package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.block.entity.BaseHoneyChargeBlockEntity;
import end3r.apielectric.block.entity.CombCapacitorBlockEntity;
import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import end3r.apielectric.block.entity.PollenTransducerBlockEntity;
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

    public static final BlockEntityType<PollenTransducerBlockEntity> POLLEN_TRANS_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(ApiElectric.MOD_ID, "pollen_transducer"),
            BlockEntityType.Builder.create(PollenTransducerBlockEntity::new, ModBlocks.POLLEN_TRANSDUCER).build(null)
    );

    public static final BlockEntityType<BaseHoneyChargeBlockEntity> BASE_HONEY_CHARGE_BLOCK_ENTITY =
            Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    new Identifier(ApiElectric.MOD_ID, "base_honey_charge_block"),
                    BlockEntityType.Builder.create(() -> new BaseHoneyChargeBlockEntity(), ModBlocks.BASE_HONEY_CHARGE_BLOCK).build(null)
            );

    public static void registerBlockEntities() {
        // Block entities are registered when the BlockEntityType is registered in the above code.
    }
}