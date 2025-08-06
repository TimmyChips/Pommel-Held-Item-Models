package timmychips.pommelheldmodels.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.resolver.RangePropertyHandler;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

public class UseDurationFloat implements RangePropertyHandler {
    public static Float test(LivingEntity entity, ItemStack stack, Float scale) {
        if (entity != null) {
            return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) * scale;
        }
        // TODO: add "remaining" field
        return  0f;
    }

    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition) {
        if (entity == null) return 0F;
        else if (entity.getActiveItem() != stack) return 0F;
        else return getTicksUsed(stack, entity);
    }

    public static int getTicksUsed(ItemStack stack, LivingEntity user) {
        return stack.getMaxUseTime(user) - user.getItemUseTimeLeft();
    }
}
