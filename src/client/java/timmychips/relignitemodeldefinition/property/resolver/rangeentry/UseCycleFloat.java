package timmychips.relignitemodeldefinition.property.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinition.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinition.property.type.RangeDispatchDefinition;

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
