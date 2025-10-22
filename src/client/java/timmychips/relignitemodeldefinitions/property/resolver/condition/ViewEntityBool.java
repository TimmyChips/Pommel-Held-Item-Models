package timmychips.relignitemodeldefinitions.property.resolver.condition;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

// Return if player is spectating entity, or if local player
public class ViewEntityBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity spectatedEntity = client.getCameraEntity(); // get entity player is spectating in spectator mode
        return spectatedEntity != null ? entity == spectatedEntity : entity == client.player; // return true if player is spectating entity or is local client player
    }
}
