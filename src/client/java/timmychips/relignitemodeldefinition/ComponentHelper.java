package timmychips.relignitemodeldefinition;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

public class ComponentHelper {
    // Needed since all items technically have something like am enchantment component, so need to check if component has changed
    public static boolean componentHasChanged(ComponentType<?> componentType, ItemStack stack) {
        ComponentChanges componentChanges = stack.getComponentChanges();

        return componentChanges.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(componentType));
    }
}
