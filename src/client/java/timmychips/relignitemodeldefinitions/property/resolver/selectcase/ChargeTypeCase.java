package timmychips.relignitemodeldefinitions.property.resolver.selectcase;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

/**
 * Returns a string value based on the loaded projectile in the crossbow.
 * <p>
 * <p>
 * <p>{@code ignore_default:} Optional. Custom field; ignores default vanilla behavior. Default: false
 * <ul>
 *     <li>Will not use value, "arrow", when crossbow has an unknown item.
 *     <li>Adds "spectral" value for crossbows loaded with spectral arrows.
 *     <li>Adds "unknown" value for non-matching projectiles if ignore_unknown is false.
 * </ul>
 * <p>{@code ignore_unknown:} Optional. Custom field; depends on ignore_default being true. Will use projectile item id as value instead of "unknown". Default: false.
 * <ul>
 *     <li>Will use "modid:item" value for any other projectile cases. Please note that this will cause unaccounted projectile cases to use the fallback model.
 *     <li>Use at own risk when all crossbow projectile types are accounted for.
 *
 */
public class ChargeTypeCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        // Safely extract the first charged projectile type
        ChargedProjectilesComponent charged = stack.get(DataComponentTypes.CHARGED_PROJECTILES);

        // Custom fields
        boolean ignore_default = definition.chargeIgnoreDefault(); // Ignores default behavior
        boolean ignore_unknown = definition.chargeIgnoreUnknown(); // Will use projectile item id as return string value instead of "unknown"

        if (charged != null && !charged.isEmpty()) {
            for (ItemStack projectile : charged.getProjectiles()) {
                Item item = projectile.getItem();

                if (item == Items.ARROW) return "arrow";
                if (item == Items.FIREWORK_ROCKET) return "rocket";

                // Will ignore default vanilla behavior; Custom values
                if (ignore_default) {
                    if (item == Items.SPECTRAL_ARROW) return "spectral";
                    if (ignore_unknown) return item.toString(); // Modded or unrecognized projectile will return id as string value
                }
            }

            if (ignore_default && !ignore_unknown) return "unknown"; // Custom value; Return if ignore_unknown field is false: Modded or unrecognized projectile.
            else return "arrow"; // Vanilla behavior; "any other projectile" case
        }
        return "none"; // no charged component on crossbow
    }
}
