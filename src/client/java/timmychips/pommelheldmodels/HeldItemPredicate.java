package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;
    public static boolean itemInOffhand = false;
    public static boolean isUsingItem = false;
    public static float isUsingItemFloat = 0.0F;
    public static Item activeItem;
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_head = "is_head";
    private static final String render_using = "is_using";

    private static final List<ModelTransformationMode> renderModeHands = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
    );

    private static HashMap<Identifier, List<ModelTransformationMode>> renderTypeWhitelist;

    public static void registerHeldModelPredicate() {

        // Creates association to render type and transformation modes
        // HashMap contains Indentifiers (held, on ground) with several mode types linked to each identifier
        renderTypeWhitelist = new HashMap<Identifier, List<ModelTransformationMode>>() {{
            put(Identifier.of(namespace, render_held), renderModeHands ); // Held render modes

            put(Identifier.of(namespace, render_offhand), renderModeHands ); // Held render modes for the offhand;

            put(Identifier.of(namespace, render_using), renderModeHands ); // Held render modes for item used;

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

                if (currentItemRenderMode == null) return 0.0F; // Return 0 if render mode is null

                boolean isOffhandPredicate = entry.getKey().getPath().equals(render_offhand); // Matches key for offhand
                boolean isUsedPredicate = entry.getKey().getPath().equals(render_using);

                // If in offhand, return 1 for the offhand predicate
                // Note that this makes is_held and is_offhand both return 1
                if (isOffhandPredicate) return (itemInOffhand && entry.getValue().contains(currentItemRenderMode)) ? 1.0F : 0.0F;

                // Predicate when player presses the use key for the using item predicate
                if (isUsedPredicate && livingEntity != null) return UseKeyTracker.player_useItemKey(livingEntity, itemStack);

                // TODO: Remove is_ground for thrown items (eggs, snowballs) and separate into two predicates: "is_ground" and a new, "is_thrown"
                //  Add a new item predicate for when player is submerged underwater "is_submerged"
                //  Probably add new predicate for falling/in air "is_falling"
                //  TBD (Probably): re-add smooth interpolation when player stops holding use key?
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
}
