package timmychips.relignitemodeldefinition.property.resolver.selectcase;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import timmychips.relignitemodeldefinition.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinition.property.type.SelectDefinition;

/**
 * Return main hand of player
 * <p>Values: left or right
 */
public class MainHandCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        Arm mainArm = MinecraftClient.getInstance().options.getSyncedOptions().mainArm();
        if (entity != null) mainArm = entity.getMainArm();

        return mainArm.toString().toLowerCase();
    }
}
