package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

public class UseDurationFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition) {
        boolean use_remaining = Boolean.TRUE.equals(definition.useRemaining());

        if (entity == null) return 0F;
        else if (entity.getActiveItem() != stack) return 0F;
        else return use_remaining ? entity.getItemUseTimeLeft() : getTicksUsed(stack, entity);
    }

    public static int getTicksUsed(ItemStack stack, LivingEntity user) {
        return stack.getMaxUseTime(user) - user.getItemUseTimeLeft();
    }
}
