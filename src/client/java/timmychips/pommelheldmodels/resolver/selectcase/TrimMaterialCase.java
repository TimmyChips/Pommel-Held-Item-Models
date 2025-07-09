package timmychips.pommelheldmodels.resolver.selectcase;

import com.mojang.logging.LogUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import org.slf4j.Logger;

public class TrimMaterialCase {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static String test(ItemStack stack) {
        ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
        return armorTrim == null ? null : armorTrim.getMaterial().getIdAsString();
    }
}
