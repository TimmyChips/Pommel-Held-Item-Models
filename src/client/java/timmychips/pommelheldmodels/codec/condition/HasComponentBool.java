package timmychips.pommelheldmodels.codec.condition;

import com.mojang.logging.LogUtils;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class HasComponentBool {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Boolean testHasComponent(String component, Boolean ignore_default, ItemStack stack) {

        Identifier componentId = Identifier.tryParse(component);
        if (componentId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", component);
            return false;
        }

        ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(componentId);
        if (componentType == null) {
            LOGGER.warn("Unknown component predicate componentType '{}'", componentId);
            return false;
        }

        if (stack.contains(componentType)) { // stack has component

            if (!ignore_default) return true;               // if ignore_default is false
            else return hasChanged(stack, componentType);   // if it's true
        }

        return false;
    }

    private static Boolean hasChanged(ItemStack stack, ComponentType<?> componentType) {
        ComponentChanges changes = stack.getComponentChanges();
        return changes.entrySet().stream()                                  // changes.entrySet returns map<ComponentType, Optional<?>>
                .anyMatch(entry -> entry.getKey().equals(componentType));   // stream and do anyMatch to check the key (ComponentType) matches to our componentType var
    }
}
