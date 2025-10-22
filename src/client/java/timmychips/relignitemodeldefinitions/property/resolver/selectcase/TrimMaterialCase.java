package timmychips.relignitemodeldefinitions.property.resolver.selectcase;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

/**
 * Return the material id of the armor trim as string
 */
public class TrimMaterialCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
        if (armorTrim == null) return null;
        return armorTrim.getMaterial().getIdAsString();
    }
}
