package timmychips.pommelheldmodels.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandleSlotAccessor {
    @Accessor("focusedSlot")
    Slot getfocusedSlot();

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();
}
