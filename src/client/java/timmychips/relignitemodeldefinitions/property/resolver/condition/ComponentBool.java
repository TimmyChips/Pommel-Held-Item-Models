package timmychips.relignitemodeldefinitions.property.resolver.condition;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

import java.util.Objects;
import java.util.Optional;

// Returns if component item sub predicate value matches specified value
public class ComponentBool implements ConditionPropertyHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        String predicate = definition.predicate(); // Retrieve item sub predicate to test for
        JsonElement value = definition.value(); // Get value to match with

        if (stack == null || predicate == null || value == null) return false;

        // Parse to Identifier
        Identifier predicateId = Identifier.tryParse(predicate);
        if (predicateId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", predicate);
            return false;
        }

        // Retrieve item sub predicate type from ID
        ItemSubPredicate.Type<?> type = Registries.ITEM_SUB_PREDICATE_TYPE.get(predicateId);
        if (type == null) {
            LOGGER.warn("Unknown component predicate type '{}'", predicateId);
            return false;
        }

        // Test match item's sub predicate value with model definition value
        try {
            DynamicOps<JsonElement> registryOps = RegistryOps.of(
                    JsonOps.INSTANCE,
                    Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getRegistryManager());

            Optional<? extends ItemSubPredicate> parsed = type.codec()
                    .decode(registryOps, value)
                    .result()
                    .map(Pair::getFirst);

            if (parsed.isPresent()) {
                return parsed.get().test(stack);
            } else {
                LOGGER.warn("Failed to decode predicate value for '{}': {}", predicateId, value);
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("Error parsing component predicate JSON for '{}': {}", predicateId, value, e);
            return false;
        }
    }
}
