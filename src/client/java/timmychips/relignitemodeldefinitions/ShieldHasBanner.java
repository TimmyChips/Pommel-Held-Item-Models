package timmychips.relignitemodeldefinitions;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public class ShieldHasBanner {
    static float getValue(ItemStack stack) {
        ComponentType<BannerPatternsComponent> bannerPatterns = DataComponentTypes.BANNER_PATTERNS; // Banner patterns on shield
        ComponentType<DyeColor> baseColor = DataComponentTypes.BASE_COLOR; // Base dye color on shield

        boolean hasBannerPatterns = stack.contains(bannerPatterns);
        boolean hasBaseColor = stack.contains(baseColor);

        if (hasBannerPatterns || hasBaseColor) {
            return ComponentHelper.componentHasChanged(bannerPatterns, stack) ||
                    ComponentHelper.componentHasChanged(baseColor, stack) ? 1F : 0F;
        }

        else return 0F;
    }
}
