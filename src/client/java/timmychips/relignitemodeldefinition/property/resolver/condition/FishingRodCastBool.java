package timmychips.relignitemodeldefinition.property.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinition.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinition.property.type.ConditionDefinition;

// Returns if player has a valid fishhook entity associated to them
public class FishingRodCastBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        if (entity instanceof PlayerEntity player) {
            return player.fishHook != null;
        }
        return false;
    }
}
