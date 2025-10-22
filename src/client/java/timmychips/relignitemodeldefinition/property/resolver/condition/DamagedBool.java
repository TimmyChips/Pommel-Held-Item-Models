package timmychips.relignitemodeldefinition.property.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinition.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinition.property.type.ConditionDefinition;

// Returns if item is damaged
public class DamagedBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        return stack.isDamaged();
    }
}
