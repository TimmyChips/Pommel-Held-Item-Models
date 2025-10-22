package timmychips.relignitemodeldefinitions.property.resolver;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinitions.property.registry.SelectPropertyRegistry;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

public class SelectValueResolver {

    public static String evaluate(
            Identifier property,
            ModelTransformationMode renderMode,
            SelectDefinition.Definition def,
            ItemStack stack,
            LivingEntity entity) {

        // TODO case "minecraft:local_time" class
        return SelectPropertyRegistry.resolve(property, stack, entity, renderMode, def);
    }
}
