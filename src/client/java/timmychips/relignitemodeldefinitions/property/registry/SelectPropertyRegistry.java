package timmychips.relignitemodeldefinitions.property.registry;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.resolver.selectcase.*;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

import java.util.HashMap;
import java.util.Map;

public class SelectPropertyRegistry {
    private static final Map<Identifier, SelectPropertyHandler> HANDLERS = new HashMap<>();

    public static void init() {
        register(Identifier.of("minecraft:block_state"), new BlockStateCase());
        register(Identifier.of("minecraft:charge_type"), new ChargeTypeCase());
        register(Identifier.of("minecraft:component"), new ComponentCase());
        register(Identifier.of("minecraft:context_dimension"), new ContextDimensionCase());
        register(Identifier.of("minecraft:context_entity_type"), new ContextEntityTypeCase());
        register(Identifier.of("minecraft:custom_model_data"), new CustomModelDataCase());
        register(Identifier.of("minecraft:display_context"), new DisplayContextCase());
        register(Identifier.of("minecraft:main_hand"), new MainHandCase());
        register(Identifier.of("minecraft:trim_material"), new TrimMaterialCase());
    }

    private static void register(Identifier id, SelectPropertyHandler handler) {
        HANDLERS.put(id, handler);
    }

    public static String resolve(Identifier id, ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        SelectPropertyHandler handler = HANDLERS.get(id);
        if (handler == null) return null;
        return handler.getValue(stack, entity, mode, definition);
    }
}
