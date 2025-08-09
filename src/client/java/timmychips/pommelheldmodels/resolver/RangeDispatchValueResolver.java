package timmychips.pommelheldmodels.resolver;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.resolver.rangeentry.RangePropertyRegistry;
import timmychips.pommelheldmodels.resolver.rangeentry.UseDurationFloat;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

public class RangeDispatchValueResolver {
    public static float evaluate(
            Identifier property, Float scale,
            ItemStack stack, LivingEntity entity,
            RangeDispatchDefinition.Definition def) {

        return RangePropertyRegistry.resolve(property, stack, entity, def) * scale;
    }
}
