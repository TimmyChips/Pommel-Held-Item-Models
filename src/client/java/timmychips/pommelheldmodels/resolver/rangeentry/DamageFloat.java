package timmychips.pommelheldmodels.resolver.rangeentry;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.resolver.RangePropertyHandler;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

public class DamageFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition) {
        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        boolean should_normalize = Boolean.TRUE.equals(definition.countNormalize());

        return should_normalize
                ? (float) damage / maxDamage // return normalized count based on item's max damage
                : Math.clamp(damage, 0F, maxDamage);
    }
}
