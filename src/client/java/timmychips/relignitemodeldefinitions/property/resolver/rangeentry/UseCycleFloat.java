package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

// Return item use left modulo use period
public class UseCycleFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        float period = def.usePeriod();

        if (entity == null) return 0F;
        if (entity.getActiveItem() == stack) return (float) entity.getItemUseTimeLeft() % period;

        return 0F;
    }
}
