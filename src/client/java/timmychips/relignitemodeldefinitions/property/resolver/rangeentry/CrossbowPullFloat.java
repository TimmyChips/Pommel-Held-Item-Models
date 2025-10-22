package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

// Return the pull use-time of the crossbow
public class CrossbowPullFloat implements RangePropertyHandler {
    @Override
    public float getValue(ItemStack stack, LivingEntity user, RangeDispatchDefinition.Definition definition) {
        if (user == null) return 0.0F;
        else if (CrossbowItem.isCharged(stack)) return 0.0F;
        else {
            int pull_time = CrossbowItem.getPullTime(stack, user);
            return (float) UseDurationFloat.getTicksUsed(stack, user) / pull_time;
//            return (float) (stack.getMaxUseTime(user) - user.getItemUseTimeLeft()) / pull_time;
        }
//        return CrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getMaxUseTime(user) - user.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(stack, user);
    }
}
