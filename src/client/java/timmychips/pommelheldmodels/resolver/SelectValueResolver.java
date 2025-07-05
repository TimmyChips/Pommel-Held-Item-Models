package timmychips.pommelheldmodels.resolver;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.resolver.selectcase.BlockStateCase;
import timmychips.pommelheldmodels.resolver.selectcase.ChargeTypeCase;

public class SelectValueResolver {

    public static String evaluate(
            Identifier property,
            ModelTransformationMode renderMode,
            @Nullable String block_state_property,
            ItemStack stack,
            LivingEntity entity) {

        String propertyStr = property.toString();

        return switch (propertyStr) {
            case "minecraft:block_state" -> BlockStateCase.test(block_state_property, stack);
            case "minecraft:display_context" -> renderMode.asString().toLowerCase();
            case "minecraft:charge_type" -> ChargeTypeCase.test(stack);

            // Add more custom properties here as needed
            default -> null;
        };
    }
}
