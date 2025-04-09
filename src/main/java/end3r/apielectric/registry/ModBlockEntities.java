package end3r.apielectric.registry;

import end3r.apielectric.block.entity.HoneyPowerConsumerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {
    public static final BlockEntityType<HoneyPowerConsumerBlockEntity> HONEY_POWER_CONSUMER =
            registerBlockEntity("honey_power_consumer", BlockEntityType.Builder.create(HoneyPowerConsumerBlockEntity::new, ModBlocks.HONEY_POWER_CONSUMER_BLOCK).build(null));

    private static <T extends BlockEntityType<?>> T registerBlockEntity(String name, T blockEntity) {
        return Registry.register(Registry.REGISTRIES.BLOCK_ENTITY_TYPE, new Identifier("apielectric", name), blockEntity);
    }

    public static void registerBlockEntities() {
        System.out.println("Registering ModBlockEntities for " + "apielectric");
    }
}
