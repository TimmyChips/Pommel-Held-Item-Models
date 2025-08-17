package timmychips.pommelheldmodels.property.resolver;

import com.mojang.logging.LogUtils;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.property.type.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ResolveRecursive {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Set<String> WARNED_MODELS = new HashSet<>();

    public static Optional<Identifier> resolve(ItemModelDefinition def, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        if (def instanceof ModelDefinition model) {
            return Optional.of(model.model());
        }

        if (def instanceof SelectDefinition.Definition select) {
            String propertyValue = SelectValueResolver.evaluate(
                    select.property(),
                    renderMode,
                    select,
                    stack,
                    entity);

            if (propertyValue != null) {
                for (SelectDefinition.Case<Identifier> c : select.cases()) {
                    if (c.when().contains(Identifier.of(propertyValue))) {
                        return resolve(c.model(), renderMode, stack, entity);
                    }
                }
            }

            if (select.fallback() == null) return missingFallbackModel(stack, select.property()); // Return warning + missing model identifier

            return resolve(select.fallback(), renderMode, stack, entity);
        }

        if (def instanceof ConditionDefinition cond) {
            boolean result = ConditionValueResolver.evaluate(
                    cond.property(),
                    stack,
                    entity,
                    cond);

            return result
                    ? resolve(cond.on_true(), renderMode, stack, entity)
                    : resolve(cond.on_false(), renderMode, stack, entity);
        }

        if (def instanceof RangeDispatchDefinition.Definition range) {
            float value = RangeDispatchValueResolver.evaluate(
                    range.property(),
                    range.scale(),
                    stack,
                    entity,
                    range);

            // Sort entries descending by threshold so highest matches first
            return range.entries().stream()
                    .sorted((a, b) -> Float.compare(b.threshold(), a.threshold()))
                    .filter(entry -> value >= entry.threshold())
                    .findFirst()
                    .map(entry -> resolve(entry.model(), renderMode, stack, entity))
                    .orElseGet(() -> {
                        if (range.fallback() != null) {
                            return resolve(range.fallback(), renderMode, stack, entity);
                        } else return missingFallbackModel(stack, range.property()); // Return warning + missing model identifier
                    });
        }

        return Optional.empty();
    }

    private static final Identifier MISSING_MODEL = Identifier.of("pommel:missingno");

    /**
     *
     * @param stack the item stack
     * @param property the property trying to fetch
     * @return Missing Identifier to render missing item model
     */
    private static Optional<Identifier> missingFallbackModel(ItemStack stack, Identifier property) {
        Set<Identifier> VALID_TYPES = Set.of(
                Identifier.of("minecraft:condition"),
                Identifier.of("minecraft:select"),
                Identifier.of("minecraft:range_threshold"));

        Item item = stack.getItem();
        String key = item.toString() + "|" + property;
        if (WARNED_MODELS.add(key)) { // true only the first time, will only print once for each unique item
            LOGGER.warn("No matching range threshold and no fallback model for property '{}', for item: '{}'", property, item);
        }
        return Optional.of(MISSING_MODEL); // Return identifier for RenderItem mixin to use to render missing model (name doesn't matter)
    }
}
