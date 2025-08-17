package timmychips.pommelheldmodels.property.resolver;

import com.mojang.logging.LogUtils;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.property.registry.ConditionPropertyRegistry;
import timmychips.pommelheldmodels.property.type.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ResolveRecursive {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Set<String> WARNED_MODELS = new HashSet<>();

    private static final Identifier MODEL = Identifier.of("minecraft:model");
    private static final Identifier CONDITION = Identifier.of("minecraft:condition");;
    private static final Identifier SELECT = Identifier.of("minecraft:select");;
    private static final Identifier RANGE = Identifier.of("minecraft:range_dispatch");;

    // TODO
    //  Make it return missing texture (missingno) if it cant find something in items model definition .json
    //  Add Warning logs for certain conditions
    //  Continue to add more condition, select, range_dispatch properties

    public static Optional<Identifier> resolve(ItemModelDefinition def, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        if (def instanceof ModelDefinition model) {
//            if (!model.type().equals(MODEL)) return unknownModelType(stack, model.type());

            return Optional.of(model.model());
        }

        if (def instanceof SelectDefinition.Definition select) {
//            if (!select.type().equals(SELECT)) return unknownModelType(stack, select.type());

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
//            if (!cond.type().equals(CONDITION)) return missingFallbackModel(stack, cond.property());

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
//            if (!range.type().equals(RANGE)) return unknownModelType(stack, range.type());

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

    private static final Set<Identifier> VALID_TYPES = Set.of(
            Identifier.of("minecraft:model"),
            Identifier.of("minecraft:condition"),
            Identifier.of("minecraft:select"),
            Identifier.of("minecraft:range_dispatch"));

    /**
     *
     * @param stack item stack
     * @param type the Identifier type
     * @return Missing item model
     */
    private static Optional<Identifier> unknownModelType(ItemStack stack, Identifier type) {
        Item item = stack.getItem();

//      EXAMPLE :: Couldn't parse item model 'minecraft:blue_dye' from pack 'file/blade-held-items_Build': Unknown element id: minecraft:select_abc
        LOGGER.error("Couldn't parse item '{}': Unknown item model type id: {}", item, type);
//        if (!VALID_TYPES.contains(type)) {
//            LOGGER.error("Couldn't parse item '{}': Unknown item model type id: {}", item, type);
//        }
        return Optional.of(MISSING_MODEL);
    }
}
