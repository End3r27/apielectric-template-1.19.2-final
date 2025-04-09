package end3r.apielectric.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup APIELECTRIC_GROUP = FabricItemGroupBuilder.create(
                    new Identifier("apielectric", "main_tab"))
            .icon(() -> new ItemStack(ModItems.HONEY_POWER_CONSUMER_ITEM))
            .build();
}
