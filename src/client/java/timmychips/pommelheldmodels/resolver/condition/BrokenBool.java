package timmychips.pommelheldmodels.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.propertyhandler.ConditionPropertyHandler;
import timmychips.pommelheldmodels.type.ConditionDefinition;

// True if item is broken (durability of 1 or below)
public class BrokenBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        return stack.getMaxDamage() - stack.getDamage() <= 1;
    }
}
