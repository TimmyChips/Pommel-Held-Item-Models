package timmychips.pommelheldmodels.type.condition;

import net.minecraft.client.option.KeyBinding;

public class KeybindDownBool {

    public static boolean testKeybind(KeyBinding keybind) {
        if (keybind == null) return false;
        return keybind.isPressed();
    }
}
