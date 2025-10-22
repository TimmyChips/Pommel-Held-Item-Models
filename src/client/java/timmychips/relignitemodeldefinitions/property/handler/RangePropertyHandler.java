package timmychips.relignitemodeldefinitions.property.handler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.type.RangeDispatchDefinition;

@FunctionalInterface
public interface RangePropertyHandler {
    float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition);
}
