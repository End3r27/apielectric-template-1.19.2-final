package end3r.apielectric.registry;

import end3r.apielectric.ApiElectric;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.PatchouliAPI;

public class ModBooks {
    // Update this to match your actual folder structure
    public static final Identifier APIELECTRIC_GUIDE = new Identifier("apielectric", "apielectric_guide");

    public static void registerBooks() {
        System.out.println("Registering ApiElectric guidebook: " + APIELECTRIC_GUIDE);
    }

    public static ItemStack getGuideBookStack() {
        try {
            return PatchouliAPI.get().getBookStack(APIELECTRIC_GUIDE);
        } catch (Exception e) {
            System.err.println("Failed to get ApiElectric guidebook: " + e.getMessage());
            return ItemStack.EMPTY;
        }
    }
}