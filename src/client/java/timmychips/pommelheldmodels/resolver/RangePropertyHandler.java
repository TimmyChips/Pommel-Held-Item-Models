package timmychips.pommelheldmodels.resolver;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

@FunctionalInterface
public interface RangePropertyHandler {
    float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition);
}
