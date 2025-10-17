package timmychips.pommelheldmodels;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ShieldHasBanner {
    static float getValue(ItemStack stack) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt != null) {
            return nbt.contains("Patterns") || nbt.contains("Base") ? 1F : 0F;
        }
        else return 0F;
    }
}
