package timmychips.pommelheldmodels.codec.condition;

import net.minecraft.client.option.KeyBinding;

public class KeybindDownProperty {

    public static boolean testKeybind(KeyBinding keybind) {
        if (keybind == null) return false;
        return keybind.isPressed();
    }
}
