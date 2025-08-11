package timmychips.pommelheldmodels.property.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.property.handler.ConditionPropertyHandler;
import timmychips.pommelheldmodels.property.type.ConditionDefinition;

// Return if entity is using an interactable item
public class UsingItemBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        if (entity == null) return false;
        return entity.isUsingItem() && entity.getActiveItem() == stack;
    }
}
