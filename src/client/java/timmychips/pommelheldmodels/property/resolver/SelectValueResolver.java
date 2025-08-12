package timmychips.pommelheldmodels.property.resolver;

import com.mojang.logging.LogUtils;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.property.registry.SelectPropertyRegistry;
import timmychips.pommelheldmodels.property.resolver.selectcase.*;
import timmychips.pommelheldmodels.property.type.SelectDefinition;

public class SelectValueResolver {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static String evaluate(
            Identifier property,
            ModelTransformationMode renderMode,
            SelectDefinition.Definition def,
            ItemStack stack,
            LivingEntity entity) {

        return SelectPropertyRegistry.resolve(property, stack, entity, def);

        /*
        String propertyStr = property.toString();

            return switch (propertyStr) {
            case "minecraft:block_state" -> BlockStateCase.test(blockStateProperty, stack);
            case "minecraft:display_context" -> renderMode.asString().toLowerCase();
            case "minecraft:charge_type" -> ChargeTypeCase.test(stack);
            case "minecraft:component" -> ComponentCase.test(component, stack);
            case "minecraft:context_dimension" -> ContextDimensionCase.test(entity);
            case "minecraft:context_entity_type" -> ContextEntityTypeCase.test(entity);
            // TODO case "minecraft:local_time" ->
            case "minecraft:main_hand" -> MainHandCase.test(entity);
            case "minecraft:trim_material" -> TrimMaterialCase.test(stack);
            case "minecraft:custom_model_data" -> CustomModelDataCase.test(stack);

            // Add more custom properties here as needed
            default -> null;
        };

         */
    }
}
