package timmychips.pommelheldmodels.type.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class SelectedBool {
    public static Boolean test(LivingEntity entity, ItemStack stack) {
        if (entity instanceof PlayerEntity player) {
            Hand hand = player.getActiveHand();
            return hand != null && player.getStackInHand(hand) == stack;
        }
        return false;
    }
}
