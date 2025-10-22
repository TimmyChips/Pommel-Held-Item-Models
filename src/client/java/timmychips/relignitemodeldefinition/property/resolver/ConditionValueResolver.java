package timmychips.relignitemodeldefinition.property.resolver;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinition.property.registry.ConditionPropertyRegistry;
import timmychips.relignitemodeldefinition.property.type.ConditionDefinition;

public class ConditionValueResolver {

    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean evaluate(
            Identifier property,
            ItemStack stack, LivingEntity entity,
            ConditionDefinition def) {

        return ConditionPropertyRegistry.resolve(property, stack, entity, def);
    }
}
