package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;
    public static boolean itemInOffhand = false;
    public static boolean isSubmerged = false;
    public static boolean isFalling = false;
    public static float isUsingItemFloat = 0.0F;
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_head = "is_head";
    private static final String render_using = "is_using";
    private static final String render_submerged = "is_submerged";

    private static final List<ModelTransformationMode> renderModeHands = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
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

    public static void registerHeldModelPredicate() {
        // Creates association to render type and transformation modes
        // HashMap contains Identifiers (held, on ground) with several mode types linked to each identifier
        renderTypeWhitelist = new HashMap<Identifier, List<ModelTransformationMode>>() {{
            put(Identifier.of(namespace, render_held), renderModeHands ); // Held render modes

            put(Identifier.of(namespace, render_offhand), renderModeHands ); // Held render modes for the offhand;

            put(Identifier.of(namespace, render_using), renderAny ); // Register for any modes for item used;
            put(Identifier.of(namespace, render_submerged), renderAny );

            put(Identifier.of(namespace, render_fixed), Arrays.asList( // Item Frame, Fixed render mode
                    ModelTransformationMode.FIXED));

            put(Identifier.of(namespace, render_ground), Arrays.asList( // Thrown item or in panda's hands
                    ModelTransformationMode.GROUND));

            put(Identifier.of(namespace, render_head), Arrays.asList( // When worn on head armor slot
                    ModelTransformationMode.HEAD));
        }};

        for (var entry:renderTypeWhitelist.entrySet()) { // Performs for each key-value pair
            // Performs for each Identifier and associated List items
            ModelPredicateProviderRegistry.register(entry.getKey(), (itemStack, world, livingEntity, i) -> { // Registers Identifier key

                boolean isOffhandPredicate = entry.getKey().getPath().equals(render_offhand); // Matches key for offhand
                boolean isUsedPredicate = entry.getKey().getPath().equals(render_using);
                boolean isSubmergedPredicate = entry.getKey().getPath().equals(render_submerged);

                if (livingEntity != null) {
                    // Predicate when player presses the use key for the using item predicate + item is in hand
                    if (isUsedPredicate && matchesItemInHand(livingEntity, itemStack)) return UseKeyTracker.playerUseItemKey(livingEntity, itemStack);

                    // Predicate when player is in water
                    if (isSubmergedPredicate) return livingEntity.isSubmergedInWater() ? 1.0F : 0.0F;
                }

                if (currentItemRenderMode == null) return 0.0F; // Return 0 if render mode is null
                // Do this after those other predicates so that they can render in the gui

                // If in offhand, return 1 for the offhand predicate
                // Note that this makes is_held and is_offhand both return 1
                if (isOffhandPredicate) return (itemInOffhand && entry.getValue().contains(currentItemRenderMode)) ? 1.0F : 0.0F;

                // TODO: Remove is_ground for thrown items (eggs, snowballs) and separate into two predicates: "is_ground" and a new, "is_thrown"
                //  Add a new item predicate for when player is submerged underwater "is_submerged"
                //  Probably add new predicate for falling/in air "is_falling"
                //  TBD: revise/change using tick cooldown for other players to a (potentially) better method?

                // Return 1 if whitelisted for all other predicates
                if (!isUsedPredicate) return entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;
                else return 0.0F;
            });
        }

//        ModelPredicateProviderRegistry.register(Items.WOODEN_PICKAXE, Identifier.ofVanilla("pull"), (itemStack, world, livingEntity, seed) -> {
//            LOGGER.info("We in the is_used predicate registry for: " + activeItem);
//            if (livingEntity == null) {
//                return 0.0F;
//            } else {
//                return livingEntity.getMainHandStack() == itemStack ? 1.0F : 0.0F;
//            }
//        });
    }

    private static boolean matchesItemInHand(LivingEntity entity, ItemStack stack) {
        ItemStack currentItem = entity.getMainHandStack().isEmpty() ? entity.getOffHandStack() : entity.getMainHandStack();
        return stack.equals(currentItem);
    }

    private static float submergedInFluidCheck(LivingEntity entity) {
        Vec3d eyePos = entity.getEyePos();
        BlockPos fluidBlock = BlockPos.ofFloored(eyePos);
        FluidState fluidState = entity.getWorld().getFluidState(fluidBlock);
        return !fluidState.isEmpty() ? 1.0F : 0.0F;
    }

    public static float isFallingCheck(LivingEntity entity) {
        if (entity.isOnGround()) return 0.0F;
        double entGrav = -1 * entity.getFinalGravity();
        double yVel = entity.getVelocity().y;
        return (yVel - entGrav) < -0.24 && entity.fallDistance > 0 ? 1.0F : 0.0F; // ensures player is moving down enough (negative y velocity) and is falling
    }
}
