package end3r.apielectric.registry;

import end3r.apielectric.bee.EnergyBeeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {

    public static final EntityType<EnergyBeeEntity> ENERGY_BEE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("apielectric", "energy_bee"),
            EntityType.Builder.create(EnergyBeeEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.7F, 0.6F) // Similar to BeeEntity
                    .maxTrackingRange(8)
                    .trackingTickInterval(2)
                    .build("energy_bee")
    );

    public static void register() {
        // Register the default attributes for the EnergyBeeEntity
        registerEntityAttributes();
    }

    // Method to register the entity's attributes
    public static void registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(ENERGY_BEE, EnergyBeeEntity.createEnergyBeeAttributes());
    }
}
