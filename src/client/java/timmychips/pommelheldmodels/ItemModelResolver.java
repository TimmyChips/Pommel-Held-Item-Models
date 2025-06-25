package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.ItemModelDefinitionCodec.*;
import java.util.Optional;

public class ItemModelResolver {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<Identifier> resolveModel(Identifier itemId, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        ItemModelDefinition def = ItemModelRegistry.get(itemId);
        if (def == null) return Optional.empty();

        return resolveRecursive(def, renderMode, stack, entity);
    }

    private static Optional<Identifier> resolveRecursive(ItemModelDefinition def, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        if (def instanceof ModelDefinition model) {
            return Optional.of(model.model());
        }

        if (def instanceof SelectDefinition select) {
            String propertyValue = resolveSelectPropertyValue(select.property(), renderMode, stack, entity);

            if (propertyValue != null) {
                for (Case c : select.cases()) {
                    if (c.when().contains(propertyValue)) {
                        return resolveRecursive(c.model(), renderMode, stack, entity);
                    }
                }
            }

            return resolveRecursive(select.fallback(), renderMode, stack, entity);
        }

        if (def instanceof ConditionDefinition cond) {
            boolean result = evaluateCondition(cond.property(), stack, entity);
            return result
                    ? resolveRecursive(cond.on_true(), renderMode, stack, entity)
                    : resolveRecursive(cond.on_false(), renderMode, stack, entity);
        }

        if (def instanceof RangeDispatchDefinition range) {
            float value = resolveRangePropertyValue(range.property(), range.scale(), stack, entity);
            float scaled = value * range.scale();

            // Sort entries descending by threshold so highest matches first
            return range.entries().stream()
                    .sorted((a, b) -> Float.compare(b.threshold(), a.threshold()))
                    .filter(entry -> value >= entry.threshold())
                    .findFirst()
                    .map(entry -> resolveRecursive(entry.model(), renderMode, stack, entity))
                    .orElseGet(() -> resolveRecursive(range.fallback(), renderMode, stack, entity));
        }

        return Optional.empty();
    }

    private static String resolveSelectPropertyValue(String property, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        return switch (property) {
            case "minecraft:display_context" -> renderMode.asString().toLowerCase();

            case "minecraft:charge_type" -> {
                // Safely extract the first charged projectile type
                var charged = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
                if (charged != null) {
                    if (charged.isEmpty()) yield "none";

                    for (ItemStack projectile : charged.getProjectiles()) {
                        Item item = projectile.getItem();

                        if (item == Items.FIREWORK_ROCKET) yield "rocket";
                        if (item == Items.SPECTRAL_ARROW) yield "spectral";
                        if (item == Items.ARROW) yield "arrow"; // not 100% accurate to vanilla; in vanilla this would mean "any other projectile" case

                        // Handle other known or custom items here if needed
                    }

                    yield "unknown"; // If modded or unrecognized projectile
                }
                yield null; // no charged component
            }

            // Add more custom properties here as needed
            default -> null;
        };
    }


    private static boolean evaluateCondition(String property, ItemStack stack, LivingEntity entity) {
        return switch (property) {
            case "minecraft:damaged" -> stack.isDamaged();

            case "minecraft:fishing_rod/cast" -> {
                if (entity instanceof PlayerEntity player) {
                    yield player.fishHook != null;
                }
                yield false;
            }

            case "minecraft:using_item" -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack;

            // Extend with more custom logic here
            default -> false;
        };
    }

    private static float resolveRangePropertyValue(String property, Float scale, ItemStack stack, LivingEntity entity) {
        return switch (property) {
            case "minecraft:use_duration" -> {
                if (entity != null) {
                    yield entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) * scale;
                }
                yield 0f;
            }

            case "minecraft:bundle/fullness" -> {
                LOGGER.info(String.valueOf(BundleItem.getAmountFilled(stack)));
                yield BundleItem.getAmountFilled(stack);
            }

//            case "minecraft:compass" -> {
//                World world = entity.getWorld();
//                LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
//                yield lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse((Object)null) : CompassItem.createSpawnPos(world);
//            }

            // Add more ranged float-returning properties here
            default -> 0f;
        };
    }
}
