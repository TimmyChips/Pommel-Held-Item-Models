package timmychips.pommelheldmodels;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Returns 1 if ItemStack is enchanted and 0 if not
 */
public class StackIsEnchanted {
    static float getValue(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            return nbt.contains("Enchantments") || nbt.contains("StoredEnchantments") ? 1F : 0F;
        }
        else return 0F;
    }
}
