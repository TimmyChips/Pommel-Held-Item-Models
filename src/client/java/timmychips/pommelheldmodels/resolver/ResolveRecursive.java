package timmychips.pommelheldmodels.resolver;

import com.mojang.logging.LogUtils;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.type.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ResolveRecursive {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> WARNED_MODELS = new HashSet<>();

    // TODO
    //  Make it return missing texture (missingno) if it cant find something in items model definition .json
    //  Add Warning logs for certain conditions
    //  Continue to add more condition, select, range_dispatch properties

    public static Optional<Identifier> resolve(ItemModelDefinition def, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        if (def instanceof ModelDefinition model) {
            return Optional.of(model.model());
        }

        if (def instanceof SelectDefinition.Definition select) {
            String propertyValue = SelectValueResolver.evaluate(select.property(), renderMode, stack, entity);

            if (propertyValue != null) {
                for (SelectDefinition.Case c : select.cases()) {
                    if (c.when().contains(propertyValue)) {
                        return resolve(c.model(), renderMode, stack, entity);
                    }
                }
            }

            return resolve(select.fallback(), renderMode, stack, entity);
        }

        if (def instanceof ConditionDefinition cond) {
            boolean result = ConditionValueResolver.evaluate(cond.property(), cond.predicate(), cond.value(), cond.component(), cond.ignore_default(), cond.keybind(), stack, entity);
            return result
                    ? resolve(cond.on_true(), renderMode, stack, entity)
                    : resolve(cond.on_false(), renderMode, stack, entity);
        }

        if (def instanceof RangeDispatchDefinition.Definition range) {
            float value = RangeDispatchValueResolver.evaluate(range.property(), range.scale(), stack, entity);

            // Sort entries descending by threshold so highest matches first
            return range.entries().stream()
                    .sorted((a, b) -> Float.compare(b.threshold(), a.threshold()))
                    .filter(entry -> value >= entry.threshold())
                    .findFirst()
                    .map(entry -> resolve(entry.model(), renderMode, stack, entity))
                    .orElseGet(() -> {
                        if (range.fallback() != null) {
                            return resolve(range.fallback(), renderMode, stack, entity);
                        } else {
                            String key = stack.getItem().toString() + "|" + range.property();
                            if (WARNED_MODELS.add(key)) { // true only the first time
                                LOGGER.warn("No matching range threshold and no fallback model for property '{}', for item: '{}'", range.property(), stack.getItem());
                            }
                            return Optional.of(Identifier.ofVanilla("missingno")); // Return missing model
                        }
                    });
        }

        return Optional.empty();
    }
}
