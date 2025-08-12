package timmychips.pommelheldmodels.property.resolver.selectcase;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import timmychips.pommelheldmodels.property.handler.SelectPropertyHandler;
import timmychips.pommelheldmodels.property.type.SelectDefinition;

public class ChargeTypeCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, SelectDefinition.Definition definition) {
        // Safely extract the first charged projectile type
        var charged = stack.get(DataComponentTypes.CHARGED_PROJECTILES);

        if (charged != null) {
            if (charged.isEmpty()) return "none";

            for (ItemStack projectile : charged.getProjectiles()) {
                Item item = projectile.getItem();

                if (item == Items.ARROW) return "arrow"; // not 100% accurate to vanilla; in vanilla this would mean "any other projectile" case
                if (item == Items.FIREWORK_ROCKET) return "rocket";
                if (item == Items.SPECTRAL_ARROW) return "spectral";
            }

            return "unknown"; // If modded or unrecognized projectile
        }

        return null; // no charged component
    }
}
