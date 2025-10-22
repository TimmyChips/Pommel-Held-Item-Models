package timmychips.relignitemodeldefinition.property.handler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinition.property.type.ConditionDefinition;

@FunctionalInterface
public interface ConditionPropertyHandler {
    boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition);
}
