package timmychips.relignitemodeldefinitions.property.handler;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

@FunctionalInterface
public interface SelectPropertyHandler {
    String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition);
}
