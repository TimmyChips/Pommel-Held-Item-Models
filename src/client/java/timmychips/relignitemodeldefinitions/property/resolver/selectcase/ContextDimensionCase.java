package timmychips.relignitemodeldefinitions.property.resolver.selectcase;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

/**
 * Returns dimension id entity is in as string
 */
public class ContextDimensionCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        if (entity == null) return null;
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        return clientWorld != null ? clientWorld.getRegistryKey().getValue().toString() : null;
    }
}
