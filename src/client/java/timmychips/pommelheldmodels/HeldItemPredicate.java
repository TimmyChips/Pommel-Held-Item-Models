package timmychips.pommelheldmodels;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.pommelheldmodels.objects.GroundItemSubmerged;
import timmychips.pommelheldmodels.objects.PredicateRenderModeMap;
import java.util.List;
import java.util.Arrays;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;
    public static boolean itemInOffhand = false;
    public static boolean isFlyingItem = false;
//    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_first_thirdperson = "first_third_person"; // 0.5F means first person, 1F means third person
    private static final String render_misc_entity_holding = "is_misc_entity_holding"; // For Villagers, Witches, Pandas, and Foxes
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_thrown = "is_thrown";
    private static final String render_head = "is_head";
    private static final String render_using = "is_using";
    private static final String render_submerged = "is_submerged";
    private static final String render_use = "item_use";

    private static final List<ModelTransformationMode> renderModeHands = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
    );

    private static final List<ModelTransformationMode> renderModeThird = Arrays.asList( // To check if current render mode is third person
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
    );

    private static final List<ModelTransformationMode> renderAny = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
            ModelTransformationMode.HEAD,
            ModelTransformationMode.FIXED,
            ModelTransformationMode.GROUND,
            ModelTransformationMode.GUI
    );

    public static final PredicateRenderModeMap predicateMap = new PredicateRenderModeMap(namespace);
    public static void predicateWhitelistMap() {
        predicateMap.addToMap(render_held, renderModeHands);
        predicateMap.addToMap(render_offhand, renderModeHands);
        predicateMap.addToMap(render_using, renderAny);
        predicateMap.addToMap(render_submerged, renderModeHands);
        predicateMap.addToMap(render_fixed, ModelTransformationMode.FIXED);
        predicateMap.addToMap(render_ground, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_thrown, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_head, ModelTransformationMode.HEAD);
        predicateMap.addToMap(render_first_thirdperson, renderModeHands);
        predicateMap.addToMap(render_misc_entity_holding, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_use, renderAny);
    }

    public static void registerHeldModelPredicate() {
        // Creates association to render type and transformation modes
        // HashMap contains Identifiers (held, on ground) with several mode types linked to each identifier
        predicateWhitelistMap();

        for (var entry:PredicateRenderModeMap.PREDICATE_RENDER_MODE_MAP.entrySet()) { // Performs for each key-value pair
            // Performs for each Identifier and associated List items
            ModelPredicateProviderRegistry.register(entry.getKey(), (itemStack, world, livingEntity, i) -> { // Registers Identifier key

                String predicate = entry.getKey().getPath();

                if (livingEntity != null) {
                    switch (predicate) {
                        case render_using -> { return matchesItemInHand(livingEntity, itemStack) ? UseKeyTracker.playerUseItemKey(livingEntity, itemStack) : 0.0F; }
                        case render_submerged -> { return livingEntity.isSubmergedInWater() ? 1.0F : 0.0F; }
                        case render_use -> { return itemUseRemaining(itemStack, livingEntity); }
                    }
                }

                // For when ItemEntity stack is in map
                if (predicate.equals(render_submerged)) return GroundItemSubmerged.SUBMERGED_MAP.contains(itemStack) ? 1.0F : 0.0F;

                if (currentItemRenderMode == null) return 0.0F; // Return 0 if render mode is null
                // Do this after those other predicates so that those can render in the gui

                return switch (predicate) {
                    case render_offhand -> itemInOffhand && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // If in offhand, return 1 for the offhand predicate
                                                                                                                            // Note that this makes is_held and is_offhand both return 1
                    case render_first_thirdperson -> firstThirdPersonCheck(); // Return float based for first_thirdperson predicate if render mode is first or third person
                    case render_misc_entity_holding -> livingEntity != null && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;
                    case render_thrown -> isFlyingItem && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // For flying/thrown items
                    case render_ground -> !isFlyingItem && livingEntity == null && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // Makes it so thrown items don't use the is_ground model
                    default -> entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // Return 1 if whitelisted for all other predicates
                };
            });
        }
    }

    public static boolean matchesItemInHand(LivingEntity entity, ItemStack stack) {
        ItemStack currentItem = entity.getMainHandStack().isEmpty() ? entity.getOffHandStack() : entity.getMainHandStack();
        return stack.toString().equals(currentItem.toString());
    }

    private static float firstThirdPersonCheck() {
        if (currentItemRenderMode.isFirstPerson()) return 0.5F;
        else if (renderModeThird.contains(currentItemRenderMode)) return 1F;
        else return 0F;
    }

    private static float itemUseRemaining(ItemStack stack, LivingEntity user) {
        if (user != null && ItemStack.areEqual(stack, user.getActiveItem())) {

            int maxUseTime = stack.getMaxUseTime();
            return (float) (maxUseTime - user.getItemUseTimeLeft()) / maxUseTime; // returns item use normalized from 0 to 1
        }
        else return 0F;
    }
}
