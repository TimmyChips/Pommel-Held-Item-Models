package timmychips.pommelheldmodels.property.registry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.property.handler.SelectPropertyHandler;
import timmychips.pommelheldmodels.property.resolver.selectcase.BlockStateCase;
import timmychips.pommelheldmodels.property.resolver.selectcase.ChargeTypeCase;
import timmychips.pommelheldmodels.property.type.SelectDefinition;

import java.util.HashMap;
import java.util.Map;

public class SelectPropertyRegistry {
    private static final Map<Identifier, SelectPropertyHandler> HANDLERS = new HashMap<>();

    public static void init() {
        register(Identifier.of("minecraft:block_state"), new BlockStateCase());
        register(Identifier.of("minecraft:charge_type"), new ChargeTypeCase());
    }

    private static void register(Identifier id, SelectPropertyHandler handler) {
        HANDLERS.put(id, handler);
    }

    public static String resolve(Identifier id, ItemStack stack, LivingEntity entity, SelectDefinition.Definition definition) {
        SelectPropertyHandler handler = HANDLERS.get(id);
        if (handler == null) return null;
        return handler.getValue(stack, entity, definition);
    }
}
