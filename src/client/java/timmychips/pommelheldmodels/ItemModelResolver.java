package timmychips.pommelheldmodels;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.mixin.networking.client.accessor.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.ItemModelDefinitionCodec.*;
import timmychips.pommelheldmodels.codec.MouseHelper;
import timmychips.pommelheldmodels.codec.StringIDHelper;
import timmychips.pommelheldmodels.codec.condition.ComponentBool;
import timmychips.pommelheldmodels.mixin.client.HandleSlotAccessor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ItemModelResolver {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> WARNED_MODELS = new HashSet<>();

    //TODO
    // Maybe move resolveModel(), resolveRecursive() methods to new, separate class?
    // Make it return missing texture (missingno) if it cant find something in items model definition .json
    // Add Warning logs for certain conditions
    // Continue to add more condition, select, range_dispatch properties
    // Add to charged_projectiles to yield string "modid:projectile_path" as well (i.e. you could specify ' "when": "mymod:bomb_arrow" ')

    //TODO (model registration)
    // Make it register custom models specified in the items model definition .json file
    // ( the "items" folder works as well for resource packs)

    //TODO (item use)
    // Add UseKeyTracker logic for non-usable items from original branch
    //   -> Fix crash from decoding packets with complex data from enchanted_books, bottles, etc.

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
            boolean result = evaluateCondition(cond.property(), cond.predicate(), cond.value(), stack, entity);
            return result
                    ? resolveRecursive(cond.on_true(), renderMode, stack, entity)
                    : resolveRecursive(cond.on_false(), renderMode, stack, entity);
        }

        if (def instanceof RangeDispatchDefinition range) {
            float value = resolveRangePropertyValue(range.property(), range.scale(), stack, entity);

            // Sort entries descending by threshold so highest matches first
            return range.entries().stream()
                    .sorted((a, b) -> Float.compare(b.threshold(), a.threshold()))
                    .filter(entry -> value >= entry.threshold())
                    .findFirst()
                    .map(entry -> resolveRecursive(entry.model(), renderMode, stack, entity))
                    .orElseGet(() -> {
                        if (range.fallback() != null) {
                            return resolveRecursive(range.fallback(), renderMode, stack, entity);
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

    private static String resolveSelectPropertyValue(String property, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        return switch (property) {
            case "minecraft:display_context" -> renderMode.asString().toLowerCase();

            case "minecraft:charge_type" -> {
                //TODO
                // Maybe add charged_projectiles to bow by getting list of valid projectiles and retrieving first one

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


    private static boolean evaluateCondition(String property, @Nullable String predicate, @Nullable JsonElement value, ItemStack stack, LivingEntity entity) {

        property = StringIDHelper.parseStringtoID(property, stack); // formats string with vanilla namespace (turns "broken" to "minecraft:broken")

        return switch (property) {
            case "minecraft:broken" -> stack.getMaxDamage() - stack.getDamage() <= 1;

            case "minecraft:carried" -> {
                boolean carrying_item = false;
                ClientPlayerEntity clientPlayer = null;

                // Try to use the rendering entity, or fallback to the client player
                if (entity instanceof ClientPlayerEntity player) {
                    clientPlayer = player;
                } else if (MinecraftClient.getInstance().player != null) {
                    clientPlayer = MinecraftClient.getInstance().player;
                }

                if (clientPlayer != null) {
                    if (clientPlayer.currentScreenHandler.getCursorStack() == stack) { // get item from cursor
                        carrying_item = true;
                    }
                }

                yield carrying_item;
            }

            case "minecraft:component" -> ComponentBool.testComponentPredicate(predicate, value, stack);

            case "minecraft:damaged" -> stack.isDamaged();

            case "minecraft:extended_view" -> Screen.hasShiftDown();

            case "minecraft:fishing_rod/cast" -> {
                if (entity instanceof PlayerEntity player) {
                    yield player.fishHook != null;
                }
                yield false;
            }

            case "pommel:hovered_item" -> {
                yield MouseHelper.isHoveredOverStack(stack, MinecraftClient.getInstance());
            }

            case "minecraft:selected" -> {
                if (entity.isPlayer()) {
                    PlayerEntity player = (PlayerEntity) entity;
                    Hand hand = player.getActiveHand();
                    yield hand != null && player.getStackInHand(hand) == stack;
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

            case "minecraft:bundle/fullness" -> BundleItem.getAmountFilled(stack);

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
