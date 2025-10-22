package timmychips.relignitemodeldefinition.property.handler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinition.property.type.RangeDispatchDefinition;

@FunctionalInterface
public interface RangePropertyHandler {
    float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition);
}
