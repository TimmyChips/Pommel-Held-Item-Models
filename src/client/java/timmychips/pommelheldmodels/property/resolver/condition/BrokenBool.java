package timmychips.pommelheldmodels.property.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.property.handler.ConditionPropertyHandler;
import timmychips.pommelheldmodels.property.type.ConditionDefinition;

// True if item is broken (durability of 1 or below)
public class BrokenBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        return stack.getMaxDamage() - stack.getDamage() <= 1;
    }
}
