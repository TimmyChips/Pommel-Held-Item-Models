package timmychips.relignitemodeldefinitions.property.resolver.condition;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

// Check if specified keybinding is held down
public class KeybindDownBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        KeyBinding keybind = definition.keybind();
        if (keybind == null) return false;
        return keybind.isPressed();
    }
}
