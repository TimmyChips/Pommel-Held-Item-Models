package timmychips.pommelheldmodels.property.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.property.handler.RangePropertyHandler;
import timmychips.pommelheldmodels.property.type.RangeDispatchDefinition;

public class BundleFullnessFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        return BundleItem.getAmountFilled(stack);
    }
}
