package timmychips.relignitemodeldefinition.property.resolver;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinition.property.registry.RangePropertyRegistry;
import timmychips.relignitemodeldefinition.property.type.RangeDispatchDefinition;

public class RangeDispatchValueResolver {
    public static float evaluate(
            Identifier property, Float scale,
            ItemStack stack, LivingEntity entity,
            RangeDispatchDefinition.Definition def) {

        return RangePropertyRegistry.resolve(property, stack, entity, def) * scale;
    }
}
