package timmychips.pommelheldmodels.propertyhandler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.type.ConditionDefinition;

@FunctionalInterface
public interface ConditionPropertyHandler {
    boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition);
}
