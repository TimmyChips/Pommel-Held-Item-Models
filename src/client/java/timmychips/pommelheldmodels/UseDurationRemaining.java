package timmychips.pommelheldmodels;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class UseDurationRemaining {

    public static float getTicksUsed(ItemStack stack, LivingEntity user) {
        if (user != null && ItemStack.areEqual(stack, user.getActiveItem())) {

            int maxUseTime = stack.getMaxUseTime(user);
            return (float) (maxUseTime - user.getItemUseTimeLeft()) / maxUseTime; // returns item use normalized from 0 to 1
        }
        else return 0F;
    }
}
