package timmychips.relignitemodeldefinition;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;

/**
 * Returns 1 if ItemStack is enchanted and 0 if not
 */
public class StackIsEnchanted {
    static float getValue(ItemStack stack) {
        ComponentType<ItemEnchantmentsComponent> enchantments = DataComponentTypes.ENCHANTMENTS;
        ComponentType<ItemEnchantmentsComponent> storedEnchantments = DataComponentTypes.STORED_ENCHANTMENTS;

        boolean hasStoredEnchantments = stack.contains(storedEnchantments);

        // Checks if stack enchantment component has changed from default, or if enchanted book has stored enchantment component
        if (stack.contains(enchantments) || hasStoredEnchantments) {
            return ComponentHelper.componentHasChanged(enchantments, stack) ||
                    hasStoredEnchantments ? 1F : 0F;
        }
        else return 0F;
    }
}
