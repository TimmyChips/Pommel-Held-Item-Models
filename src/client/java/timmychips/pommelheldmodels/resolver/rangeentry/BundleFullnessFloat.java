package timmychips.pommelheldmodels.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.resolver.RangePropertyHandler;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

public class BundleFullnessFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        return BundleItem.getAmountFilled(stack);
    }
}
