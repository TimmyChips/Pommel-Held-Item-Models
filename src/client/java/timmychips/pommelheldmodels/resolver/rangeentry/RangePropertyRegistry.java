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

    // Register each property
    public static void init() {
        register(Identifier.of("minecraft:bundle/fullness"), new BundleFullnessFloat());
        register(Identifier.of("minecraft:compass"), new CompassFloat());
        register(Identifier.of("minecraft:cooldown"), new CooldownFloat());
        register(Identifier.of("minecraft:count"), new CountFloat());
        register(Identifier.of("minecraft:crossbow/pull"), new CrossbowPullFloat());
        register(Identifier.of("minecraft:damage"), new DamageFloat());
    }

    public static void register(Identifier id, RangePropertyHandler handler) {
        HANDLERS.put(id, handler);
    }

    public static float resolve(Identifier id, ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        RangePropertyHandler handler = HANDLERS.get(id);
        if (handler == null) return 0f;
        return handler.getValue(stack, entity, def);
    }
}
