package timmychips.pommelheldmodels.resolver.rangeentry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.resolver.RangePropertyHandler;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

import java.util.HashMap;
import java.util.Map;

public class RangePropertyRegistry {
    private static final Map<Identifier, RangePropertyHandler> HANDLERS = new HashMap<>();

    public static void register(String id, RangePropertyHandler handler) {
        HANDLERS.put(Identifier.of(id), handler);
    }

    public static float resolve(Identifier id, ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        RangePropertyHandler handler = HANDLERS.get(id);
        if (handler == null) return 0f;
        return handler.getValue(stack, entity, def);
    }
}
