package timmychips.relignitemodeldefinitions.property.resolver.condition;

import com.mojang.logging.LogUtils;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

// Returns if item has specified component (and/or if it should ignore the default component value)
public class HasComponentBool implements ConditionPropertyHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        String component = definition.component(); // Specified component string from items definition
        boolean ignore_default = definition.ignore_default(); // If it should ignore the default item's component value

        if (component == null) return false;

        // Parse string to Identifier id
        Identifier componentId = Identifier.tryParse(component);
        if (componentId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", component);
            return false;
        }

        // Get component type from Identifier
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

    // Boolean if item component has had component changes
    private static Boolean hasChanged(ItemStack stack, ComponentType<?> componentType) {
        ComponentChanges changes = stack.getComponentChanges();
        return changes.entrySet().stream()                                  // changes.entrySet returns map<ComponentType, Optional<?>>
                .anyMatch(entry -> entry.getKey().equals(componentType));   // stream and do anyMatch to check the key (ComponentType) matches to our componentType var
    }
}
