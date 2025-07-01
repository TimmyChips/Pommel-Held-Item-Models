package timmychips.pommelheldmodels.type.condition;

import net.minecraft.item.ItemStack;

public class BrokenBool {
    public static Boolean testIsBroken(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamage() <= 1;
    }
}
