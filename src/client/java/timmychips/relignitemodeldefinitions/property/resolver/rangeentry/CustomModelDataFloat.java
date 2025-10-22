package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import com.mojang.logging.LogUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.resolver.ResolveRecursive;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

import java.util.Set;

public class CustomModelDataFloat implements RangePropertyHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> WARNED_MODELS = ResolveRecursive.WARNED_MODELS;

    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition) {
        // Warning that custom_model_data is only an integer in versions below 1.21.4
        String key = stack.getItem().toString() + "|" + "minecraft:custom_model_data";
        if (WARNED_MODELS.add(key)) LOGGER.warn("Unable to read 'custom_model_data' for type: 'minecraft:range_dispatch' since component is an integer in this version. Defaulting to use integer values.");

        CustomModelDataComponent custom_model_data = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return custom_model_data != null ? (float) custom_model_data.value() : 0F;
    }
}
