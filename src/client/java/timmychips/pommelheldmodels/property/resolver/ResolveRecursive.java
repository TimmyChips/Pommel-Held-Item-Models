package timmychips.pommelheldmodels.property.resolver;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.property.type.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ResolveRecursive {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Set<String> WARNED_MODELS = ConcurrentHashMap.newKeySet();
    private static final Identifier MISSING_MODEL_ID = Identifier.of("pommel:missingno");

    /** Lazily fetch the baked model manager */
    private static FabricBakedModelManager getBakedModelManager() {
        return MinecraftClient.getInstance().getBakedModelManager();
    }

    /** Fetch missing model safely */
    public static BakedModel getMissingModel() {
        return getBakedModelManager().getModel(MISSING_MODEL_ID);
    }

    /**
     * Resolves a definition recursively into a baked model.
     */
    public static Optional<BakedModel> resolve(ItemModelDefinition def, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        if (def == null) return Optional.empty();
        if (renderMode == null) renderMode = ModelTransformationMode.GUI;

        FabricBakedModelManager manager = getBakedModelManager();

        if (def instanceof ModelDefinition model) {
            return Optional.of(manager.getModel(model.model()));
        }

        if (def instanceof CompositeModelDefinition composite) {
            return Optional.of(composite.bake(manager));

//            List<BakedModel> bakedParts = composite.models().stream()
//                    .map(manager::getModel)
//                    .toList();
//            if (bakedParts != null) return Optional.of(new CompositeItemModel(bakedParts));
//            return Optional.of(new CompositeItemModel(bakedParts));
        }

        if (def instanceof SelectDefinition.Definition select) {
            String propertyValue = SelectValueResolver.evaluate(select.property(), renderMode, select, stack, entity);

            if (propertyValue != null) {
                for (SelectDefinition.Case<Identifier> c : select.cases()) {
                    if (c.when().contains(Identifier.of(propertyValue))) {
                        return resolve(c.model(), renderMode, stack, entity);
                    }
                }
            }

            return select.fallback() != null
                    ? resolve(select.fallback(), renderMode, stack, entity)
                    : missingFallbackModel(stack, select.property());
        }

        if (def instanceof ConditionDefinition cond) {
            boolean result = ConditionValueResolver.evaluate(cond.property(), stack, entity, cond);
            return result
                    ? resolve(cond.on_true(), renderMode, stack, entity)
                    : resolve(cond.on_false(), renderMode, stack, entity);
        }

        if (def instanceof RangeDispatchDefinition.Definition range) {
            float value = RangeDispatchValueResolver.evaluate(range.property(), range.scale(), stack, entity, range);

            ModelTransformationMode finalRenderMode = renderMode;
            return range.entries().stream()
                    .sorted((a, b) -> Float.compare(b.threshold(), a.threshold())) // highest threshold first
                    .filter(entry -> value >= entry.threshold())
                    .findFirst()
                    .map(entry -> resolve(entry.model(), finalRenderMode, stack, entity))
                    .orElseGet(() -> range.fallback() != null
                            ? resolve(range.fallback(), finalRenderMode, stack, entity)
                            : missingFallbackModel(stack, range.property()));
        }

        return missingFallbackModel(stack, null);
    }

    /** Warn once and return missing model if no match found */
    private static Optional<BakedModel> missingFallbackModel(ItemStack stack, Identifier property) {
        String key = stack.getItem().toString() + "|" + property;
        if (WARNED_MODELS.add(key)) {
            LOGGER.warn("No matching model found for property '{}', item: '{}'", property, stack.getItem());
        }
        return Optional.of(getMissingModel());
    }
}
