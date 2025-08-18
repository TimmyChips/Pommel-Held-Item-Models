package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;
    public static boolean itemInOffhand = false;
    public static boolean isFlyingItem = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_first_thirdperson = "first_third_person"; // 0F means first person, 1F means third person
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_thrown = "is_thrown";
    private static final String render_head = "is_head";
    private static final String render_using = "is_using";
    private static final String render_submerged = "is_submerged";

    private static final List<ModelTransformationMode> renderModeHands = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
    );

    private static final List<ModelTransformationMode> renderModeFirst = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
    );

    private static final List<ModelTransformationMode> renderModeThird = Arrays.asList(
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

    private static HashMap<Identifier, List<ModelTransformationMode>> renderTypeWhitelist;
    public static PredicateRenderModeMap predicateMap = new PredicateRenderModeMap(namespace);

    public static void predicateWhitelistMap() {
        predicateMap.addToMap(render_held, renderModeHands);
        predicateMap.addToMap(render_offhand, renderModeHands);
        predicateMap.addToMap(render_using, renderAny);
        predicateMap.addToMap(render_submerged, renderModeHands);
        predicateMap.addToMap(render_fixed, ModelTransformationMode.FIXED);
        predicateMap.addToMap(render_ground, ModelTransformationMode.GROUND);
        predicateMap.addToMap(render_head, ModelTransformationMode.HEAD);
    }

    public static void registerHeldModelPredicate() {
        // Creates association to render type and transformation modes
        // HashMap contains Identifiers (held, on ground) with several mode types linked to each identifier
        predicateWhitelistMap();

        for (var entry:PredicateRenderModeMap.PREDICATE_RENDER_MODE_MAP.entrySet()) { // Performs for each key-value pair
            // Performs for each Identifier and associated List items
            ModelPredicateProviderRegistry.register(entry.getKey(), (itemStack, world, livingEntity, i) -> { // Registers Identifier key

                boolean isOffhandPredicate = entry.getKey().getPath().equals(render_offhand); // Matches key for offhand
                boolean isUsedPredicate = entry.getKey().getPath().equals(render_using);
                boolean isSubmergedPredicate = entry.getKey().getPath().equals(render_submerged);
                boolean isGroundPredicate = entry.getKey().getPath().equals(render_ground);
                boolean isThrownPredicate = entry.getKey().getPath().equals(render_thrown);

                if (livingEntity != null) {
                    // Predicate when player presses the use key for the using item predicate + item is in hand
                    if (isUsedPredicate && matchesItemInHand(livingEntity, itemStack)) return UseKeyTracker.playerUseItemKey(livingEntity, itemStack);

                    // Predicate when player is in water
                    if (isSubmergedPredicate) return livingEntity.isSubmergedInWater() ? 1.0F : 0.0F;
                }

                if (currentItemRenderMode == null) return 0.0F; // Return 0 if render mode is null
                // Do this after those other predicates so that those can render in the gui

                // If in offhand, return 1 for the offhand predicate
                // Note that this makes is_held and is_offhand both return 1
                if (isOffhandPredicate) return (itemInOffhand && entry.getValue().contains(currentItemRenderMode)) ? 1.0F : 0.0F;

                // For flying/thrown items
                if (isThrownPredicate) return isFlyingItem && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;
                // Makes it so thrown items don't use the is_ground model
                if (isGroundPredicate) return  !isFlyingItem && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;

                // Return 1 if whitelisted for all other predicates
                if (!isUsedPredicate) return entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;
                else return 0.0F;
            });
        }
    }

    public static boolean matchesItemInHand(LivingEntity entity, ItemStack stack) {
        ItemStack currentItem = entity.getMainHandStack().isEmpty() ? entity.getOffHandStack() : entity.getMainHandStack();
        return stack.toString().equals(currentItem.toString());
    }
}
