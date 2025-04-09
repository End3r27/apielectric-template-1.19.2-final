package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import end3r.apielectric.bee.EnergyBeeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {

    // Declare the entity type
    public static final EntityType<EnergyBeeEntity> ENERGY_BEE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(ApiElectric.MOD_ID, "energy_bee"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EnergyBeeEntity::new)
                    .dimensions(EntityType.PIG.getDimensions()) // Use the dimensions of a vanilla entity like PIG, or specify your own
                    .build()
    );

    public static void registerEntities() {
        // The entity is already registered during the FabricEntityTypeBuilder creation
    }
}
