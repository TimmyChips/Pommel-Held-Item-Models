package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

// Return Stack Size
public class CountFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition) {
        int count = stack.getCount();
        int maxStack = stack.getMaxCount();
        boolean should_normalize = Boolean.TRUE.equals(definition.countNormalize());

        return should_normalize ?
                (float) count / maxStack // return count divided by minecraft:max_stack_size component, clamped to 0.0 to 1.0
                : Math.clamp(count, 0F, maxStack); // return count clamped from 0 to minecraft:max_stack_size
    }
}
