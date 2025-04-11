package end3r.apielectric.registry;

import end3r.apielectric.bee.EnergyBeeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {

    public static final EntityType<EnergyBeeEntity> ENERGY_BEE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("apielectric", "energy_bee"),
            EntityType.Builder.<EnergyBeeEntity>create(EnergyBeeEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.7F, 0.6F)  // Adjusting bee size
                    .maxTrackingRange(8)
                    .trackingTickInterval(2)
                    .build("energy_bee")
    );

    public static void register() {
        // Register the default attributes for the EnergyBeeEntity
        FabricDefaultAttributeRegistry.register(ENERGY_BEE, EnergyBeeEntity.createEnergyBeeAttributes());
    }
}
