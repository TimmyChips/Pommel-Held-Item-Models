package timmychips.pommelheldmodels.resolver;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.type.selectcase.ChargeTypeCase;

public class SelectValueResolver {

    public static String evaluate(Identifier property, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {

        String propertyStr = property.toString();

        return switch (propertyStr) {
            case "minecraft:display_context" -> renderMode.asString().toLowerCase();
            case "minecraft:charge_type" -> ChargeTypeCase.test(stack);

            // Add more custom properties here as needed
            default -> null;
        };
    }
}
