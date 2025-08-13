package timmychips.pommelheldmodels.property.resolver.selectcase;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.property.handler.SelectPropertyHandler;
import timmychips.pommelheldmodels.property.type.SelectDefinition;

/**
 * Returns entity as string
 */
public class ContextEntityTypeCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        if (entity == null) return null;
        return entity.getType().getRegistryEntry().registryKey().getValue().toString();
    }
}
