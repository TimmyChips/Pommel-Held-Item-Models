package timmychips.pommelheldmodels.mixin.client;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ComponentChanges.class)
public interface ComponentChangesAccessor {
     @Accessor("changedComponents")
     Reference2ObjectMap<ComponentType<?>, Optional<?>> getChangedComponents();
}
