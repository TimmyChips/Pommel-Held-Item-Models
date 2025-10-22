package timmychips.relignitemodeldefinitions.property.resolver.selectcase;

import com.mojang.logging.LogUtils;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

/**
 * Returns a string for specified component's value
 * <p>{@code component:} ID of the component type
 */
public class ComponentCase implements SelectPropertyHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        String component = definition.component(); // Retrieve specified component to check for
        if (component == null) return null;

        Identifier componentId = Identifier.tryParse(component); // Parse string to id
        if (componentId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", component);
            return null;
        }

        ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(componentId); // Retrieve component type from id
        if (componentType == null) {
            LOGGER.warn("Unknown component predicate componentType '{}'", componentId);
            return null;
        }

        String str;

        Object componentValue = stack.get(componentType);
        if (componentValue instanceof Text textValue) {
            str = textValue.getString(); // Get the string without the surrounding literal from Text component types, and with string as is
        }
        else str = String.valueOf(componentValue).toLowerCase(); // Convert value to lower case string

        return str; // Return component value as string
    }
}
