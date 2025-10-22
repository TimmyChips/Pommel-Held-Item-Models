package timmychips.relignitemodeldefinitions.property.registry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.resolver.rangeentry.*;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

import java.util.HashMap;
import java.util.Map;

public class RangePropertyRegistry {
    private static final Map<Identifier, RangePropertyHandler> HANDLERS = new HashMap<>();

    // TODO
    //  Reorganize project structure to have "property" folder with "handler", "resolver", "type", and "register"
    //  Refactor other parts of code to new interface object system

    // Register each property
    public static void init() {
        register(Identifier.of("minecraft:bundle/fullness"), new BundleFullnessFloat());
        register(Identifier.of("minecraft:compass"), new CompassFloat());
        register(Identifier.of("minecraft:cooldown"), new CooldownFloat());
        register(Identifier.of("minecraft:count"), new CountFloat());
        register(Identifier.of("minecraft:crossbow/pull"), new CrossbowPullFloat());
        register(Identifier.of("minecraft:damage"), new DamageFloat());
        register(Identifier.of("minecraft:time"), new ClockTimeFloat());
        register(Identifier.of("minecraft:use_cycle"), new UseCycleFloat());
        register(Identifier.of("minecraft:use_duration"), new UseDurationFloat());
        register(Identifier.of("minecraft:custom_model_data"), new CustomModelDataFloat());
    }

    private static void register(Identifier id, RangePropertyHandler handler) {
        HANDLERS.put(id, handler);
    }

    public static float resolve(Identifier id, ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        RangePropertyHandler handler = HANDLERS.get(id);
        if (handler == null) return 0f;
        return handler.getValue(stack, entity, def);
    }
}
