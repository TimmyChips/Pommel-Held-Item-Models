package timmychips.pommelheldmodels.property.resolver.selectcase;

import com.mojang.logging.LogUtils;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Objects;

public class ComponentCase {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static String test(String component, ItemStack stack) {
        Identifier componentId = Identifier.tryParse(component);
        if (componentId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", component);
            return null;
        }

        ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(componentId);
        if (componentType == null) {
            LOGGER.warn("Unknown component predicate componentType '{}'", componentId);
            return null;
        }

//        LOGGER.info(Objects.requireNonNull(stack.get(componentType)).toString());
        return Objects.requireNonNull(stack.get(componentType)).toString().toLowerCase();
    }
}
