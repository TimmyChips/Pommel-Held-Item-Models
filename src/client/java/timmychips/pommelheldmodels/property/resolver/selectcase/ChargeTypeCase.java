package timmychips.pommelheldmodels.property.resolver.selectcase;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ChargeTypeCase {
    public static String test(ItemStack stack) {
        //TODO
        // Maybe add charged_projectiles to bow by getting list of valid projectiles and retrieving first one
        // Add to charged_projectiles to return string "modid:projectile_path" as well (i.e. you could specify ' "when": "mymod:bomb_arrow" ')

        // Safely extract the first charged projectile type
        var charged = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (charged != null) {
            if (charged.isEmpty()) return  "none";

            for (ItemStack projectile : charged.getProjectiles()) {
                Item item = projectile.getItem();

                if (item == Items.FIREWORK_ROCKET) return "rocket";
                if (item == Items.SPECTRAL_ARROW) return "spectral";
                if (item == Items.ARROW) return "arrow"; // not 100% accurate to vanilla; in vanilla this would mean "any other projectile" case

                // Handle other known or custom items here if needed
            }

            return "unknown"; // If modded or unrecognized projectile
        }
        return null; // no charged component
    }
}
