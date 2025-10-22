package timmychips.relignitemodeldefinitions.property.registry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.resolver.condition.*;
import timmychips.relignitemodeldefinitions.property.resolver.condition.custom.HoveredItemBool;
import timmychips.relignitemodeldefinitions.property.resolver.condition.custom.SubmergedBool;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

import java.util.HashMap;
import java.util.Map;

public class ConditionPropertyRegistry {
    private static final Map<Identifier, ConditionPropertyHandler> HANDLERS = new HashMap<>();

    public static void init() {
        register(Identifier.of("minecraft:broken"), new BrokenBool());
        register(Identifier.of("minecraft:carried"), new CarriedBool());
        register(Identifier.of("minecraft:component"), new ComponentBool());
        register(Identifier.of("minecraft:custom_model_data"), new CustomModelDataBool());
        register(Identifier.of("minecraft:damaged"), new DamagedBool());
        register(Identifier.of("minecraft:extended_view"), new ExtendedViewBool());
        register(Identifier.of("minecraft:fishing_rod/cast"), new FishingRodCastBool());
        register(Identifier.of("minecraft:has_component"), new HasComponentBool());
        register(Identifier.of("minecraft:keybind_down"), new KeybindDownBool());
        register(Identifier.of("minecraft:selected"), new SelectedBool());
        register(Identifier.of("minecraft:using_item"), new UsingItemBool());
        register(Identifier.of("minecraft:view_entity"), new ViewEntityBool());

        // Custom, modded Properties
        register(Identifier.of("pommel:hovered_item"), new HoveredItemBool());
        register(Identifier.of("pommel:submerged"), new SubmergedBool());
    }

    private static void register(Identifier id, ConditionPropertyHandler handler) {
        HANDLERS.put(id, handler);
    }

    public static boolean resolve(Identifier id, ItemStack stack, LivingEntity entity, ConditionDefinition def) {
        ConditionPropertyHandler handler = HANDLERS.get(id);
        if (handler == null) return false;
        return handler.getValue(stack, entity, def);
    }
}
