package timmychips.relignitemodeldefinitions.property.resolver.selectcase;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

/**
 * Return custom model data int component value on item as string
 * <p>(Closest to custom_model_data predicate, pre-1.21.4)
 */
public class CustomModelDataCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        CustomModelDataComponent custom_model_data = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (custom_model_data == null) return null;
        return String.valueOf(custom_model_data.value());
    }
}
