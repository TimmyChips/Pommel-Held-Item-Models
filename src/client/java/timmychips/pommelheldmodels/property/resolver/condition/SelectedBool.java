package timmychips.pommelheldmodels.property.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import timmychips.pommelheldmodels.property.handler.ConditionPropertyHandler;
import timmychips.pommelheldmodels.property.type.ConditionDefinition;

// Return if item is selected in player's hand
public class SelectedBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        if (entity instanceof PlayerEntity player) {
            Hand hand = player.getActiveHand();
            return hand != null && player.getStackInHand(hand) == stack;
        }
        return false;
    }
}
