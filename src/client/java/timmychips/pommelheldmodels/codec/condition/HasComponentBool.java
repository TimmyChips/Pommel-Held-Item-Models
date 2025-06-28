package timmychips.pommelheldmodels.codec.condition;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.mixin.client.ComponentChangesAccessor;

import java.util.Optional;

public class HasComponentBool {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Boolean testHasComponent(String component, @Nullable Boolean ignore_default, ItemStack stack) {

        Identifier componentId = Identifier.tryParse(component);
        if (componentId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", component);
            return false;
        }

        ComponentType<?> type = Registries.DATA_COMPONENT_TYPE.get(componentId);
        if (type == null) {
            LOGGER.warn("Unknown component predicate type '{}'", componentId);
            return false;
        }

        if (stack.contains(type)) {
            LOGGER.info(String.valueOf(!stack.isEmpty() && hasChangedComponent(type)));
            LOGGER.info("Has component: {}", type);
            return true;
        }

        return false;
    }

    private static boolean hasChangedComponent(ComponentType<?> type) {
        Reference2ObjectMap<ComponentType<?>, Optional<?>> hasComponentType = ((ComponentChangesAccessor) type).getChangedComponents();
        return hasComponentType.containsKey(type);
    }
}
