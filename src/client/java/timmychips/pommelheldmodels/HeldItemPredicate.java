package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
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
    public static boolean itemBeingUsed = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_head = "is_head";
    private static final String render_used = "is_used";

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

                // If in offhand, return 1 for the offhand predicate
                // Note that this makes is_held and is_offhand both return 1
                if (isOffhandPredicate) return (itemInOffhand && entry.getValue().contains(currentItemRenderMode)) ? 1.0F : 0.0F;


                // Return 1 if whitelisted for all other predicates
                return entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;
            });
        }
    }
}
