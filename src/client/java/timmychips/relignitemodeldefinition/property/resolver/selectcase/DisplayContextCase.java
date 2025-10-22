package timmychips.relignitemodeldefinition.property.resolver.selectcase;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinition.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinition.property.type.SelectDefinition;

/**
 * Return current item model transformation mode as lower case string
 */
public class DisplayContextCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        return mode.asString().toLowerCase();
    }
}
