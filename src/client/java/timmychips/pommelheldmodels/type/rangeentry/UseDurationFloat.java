package timmychips.pommelheldmodels.type.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class UseDurationFloat {
    public static Float test(LivingEntity entity, ItemStack stack, Float scale) {
        if (entity != null) {
            return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) * scale;
        }
        return  0f;
    }
}
