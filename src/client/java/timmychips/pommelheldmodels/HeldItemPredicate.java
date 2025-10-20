package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.objects.GroundItemSubmerged;
import timmychips.pommelheldmodels.objects.PredicateRenderModeMap;
import java.util.List;
import java.util.Arrays;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;
    public static boolean itemInOffhand = false;
    public static boolean isProjectile = false; // for thrown eggs, snowballs, and projectiles like arrows
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_first_thirdperson = "first_third_person"; // 0.5F means first person, 1F means third person
    private static final String render_misc_entity_holding = "is_misc_entity_holding"; // For Villagers, Witches, Pandas, and Foxes
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_projectile = "is_projectile";
    private static final String render_projectile_backwardsCompat = "is_flying"; // Old is_flying projectile predicate backwards compatibility
    private static final String render_head = "is_head";
    private static final String render_using = "is_using";
    private static final String render_submerged = "is_submerged";
    private static final String render_use = "item_use";
    private static final String render_enchanted = "is_enchanted";
    private static final String render_shield_banner = "shield_has_banner"; // If shield has a banner pattern or base color component

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
        predicateMap.addToMap(render_projectile, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_projectile_backwardsCompat, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_head, ModelTransformationMode.HEAD);
        predicateMap.addToMap(render_first_thirdperson, renderModeHands);
        predicateMap.addToMap(render_misc_entity_holding, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_use, renderAny);
        predicateMap.addToMap(render_enchanted, renderAny);
        predicateMap.addToMap(render_shield_banner, renderAny);
    }

    public static void registerHeldModelPredicate() {
        // Creates association to render type and transformation modes
        // HashMap contains Identifiers (held, on ground) with several mode types linked to each identifier
        predicateWhitelistMap();

        for (var entry:PredicateRenderModeMap.PREDICATE_RENDER_MODE_MAP.entrySet()) { // Performs for each key-value pair
            // Performs for each Identifier and associated List items
            ModelPredicateProviderRegistry.register(entry.getKey(), (itemStack, world, livingEntity, i) -> { // Registers Identifier key

                String predicate = entry.getKey().getPath();

                // Backwards compatibility with old 'is_flying' predicate, sets it to new predicate name
                if (predicate.equals(render_projectile_backwardsCompat)) predicate = render_projectile;

        /// For all render modes for LivingEntities and Entities
            // Predicate effects all entities (such as item entities) in any render mode (e.g. gui)
                if (predicate.equals(render_enchanted)) return StackIsEnchanted.getValue(itemStack);
            // Predicate if shield has banner attached to it
                if (predicate.equals(render_shield_banner)) return ShieldHasBanner.getValue(itemStack);

            // Predicate only affects living entities in any render mode (e.g. gui)
                if (livingEntity != null) {
                    switch (predicate) {
                        case render_using -> { return matchesItemInHand(livingEntity, itemStack) ? UseKeyTracker.playerUseItemKey(livingEntity, itemStack) : 0.0F; }
                        case render_submerged -> { return livingEntity.isSubmergedInWater() ? 1.0F : 0.0F; }
                        case render_use -> { return itemUseRemaining(itemStack, livingEntity); }
                    }
                }

            // For when ItemEntity stack is in submerged map
                if (predicate.equals(render_submerged)) return GroundItemSubmerged.SUBMERGED_MAP.contains(itemStack) ? 1.0F : 0.0F;

        /// For all non-gui render modes
            // Predicate effects any render mode that is not gui
                if (currentItemRenderMode == null) return 0.0F; // Return 0 if render mode is null
                // Do this after those other predicates so that those can render in the gui

                boolean isItemEntity = itemStack.getHolder() instanceof ItemEntity; // Gets stack holder entity and checks if it's the holder is an item entity

                return switch (predicate) {
                    case render_offhand -> itemInOffhand && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // If in offhand, return 1 for the offhand predicate
                                                                                                                            // Note that this makes is_held and is_offhand both return 1
                    case render_first_thirdperson -> firstThirdPersonCheck(); // Return float based for first_thirdperson predicate if render mode is first or third person
                    case render_misc_entity_holding -> livingEntity != null && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;
                    // Compat with 2D Projectiles mod; Makes projectiles like arrows use specified item model as well
                    case render_projectile -> {
                        if (isProjectile && !isItemEntity && livingEntity == null) { // For non- LivingEntity's nor an ItemEntity's to make sure its only projectiles
                            isProjectile = false; // Reset variable
                            yield 1F;
                        }
                        else yield 0F;
                    } // For thrown item entities and projectiles
                    case render_ground -> isItemEntity && livingEntity == null && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // Makes it so thrown items don't use the is_ground model
                    default -> entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F; // Return 1 if whitelisted for all other predicates
                };
            });
        }
    }

    /**
     *
     * @param entity Current LivingEntity
     * @param stack Current ItemStack
     * @return True/False if the ItemStack field is actively held in main hand or offhand
     */
    public static boolean matchesItemInHand(LivingEntity entity, ItemStack stack) {
        ItemStack currentItem = entity.getMainHandStack().isEmpty() ? entity.getOffHandStack() : entity.getMainHandStack();
        return stack.toString().equals(currentItem.toString());
    }

    /**
     * Predicate for if item model is in first person or third person
     *
     * @return 0.5F if current render mode is first person, or 1F if in third person. 0F if neither.
     */
    private static float firstThirdPersonCheck() {
        if (currentItemRenderMode.isFirstPerson()) return 0.5F;
        else if (renderModeThird.contains(currentItemRenderMode)) return 1F;
        else return 0F;
    }

    /**
     *
     * Change predicate if entity is using an interactable item that has a use time (shield, food, bow, etc.)
     *
     * @param stack Current ItemStack
     * @param user The LivingEntity who is holding the item
     * @return Current ItemStack use normalized from 0 to 1
     */
    private static float itemUseRemaining(ItemStack stack, LivingEntity user) {
        if (user != null && ItemStack.areEqual(stack, user.getActiveItem())) {

            int maxUseTime = stack.getMaxUseTime(user);
            return (float) (maxUseTime - user.getItemUseTimeLeft()) / maxUseTime; // returns item use normalized from 0 to 1
        }
        else return 0F;
    }
}
