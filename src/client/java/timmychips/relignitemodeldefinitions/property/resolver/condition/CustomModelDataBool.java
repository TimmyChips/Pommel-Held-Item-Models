package timmychips.relignitemodeldefinitions.property.resolver.condition;

import com.mojang.logging.LogUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.resolver.ResolveRecursive;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

import java.util.Set;

// Return true if item has custom model data component
public class CustomModelDataBool implements ConditionPropertyHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
//    private static final Set<String> WARNED_MODELS = new HashSet<>();
    private static final Set<String> WARNED_MODELS = ResolveRecursive.WARNED_MODELS;

    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        // Warning that custom_model_data is only an integer in versions below 1.21.4
        String key = stack.getItem().toString() + "|" + "minecraft:custom_model_data";
        if (WARNED_MODELS.add(key)) LOGGER.warn("Unable to read 'custom_model_data' for type: 'minecraft:condition' since component is an integer in this version. Defaulting to be true if component is present on item.");

        // Will still check if custom_model_data component is there
        var custom_model_data = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return custom_model_data != null;
    }
}
