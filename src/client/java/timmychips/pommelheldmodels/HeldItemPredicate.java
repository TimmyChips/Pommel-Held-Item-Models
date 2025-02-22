package timmychips.pommelheldmodels;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;

    //
    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_head = "is_head";


    //
    private static HashMap<Identifier, List<ModelTransformationMode>> renderTypeWhitelist;

    public static void registerHeldModelPredicate() {

        // Creates association to render type and transformation modes
        // HashMap contains Indentifiers (held, on ground) with several mode types linked to each identifier
        renderTypeWhitelist = new HashMap<Identifier, List<ModelTransformationMode>>() {{
            put(Identifier.of(namespace, render_held), Arrays.asList( // Held render modes
                    ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
                    ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
                    ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
                    ModelTransformationMode.THIRD_PERSON_RIGHT_HAND));

            put(Identifier.of(namespace, render_fixed), Arrays.asList( // Item Frame, Fixed render mode
                    ModelTransformationMode.FIXED));

            put(Identifier.of(namespace, render_ground), Arrays.asList( // Thrown on ground render mod
                    ModelTransformationMode.GROUND));

            put(Identifier.of(namespace, render_head), Arrays.asList( // When worn on head armor slot
                    ModelTransformationMode.HEAD));
        }};

        for (var entry:renderTypeWhitelist.entrySet()) { // Performs for each key-value pair
                                                         // Performs for each Identifier and associated List items

            ModelPredicateProviderRegistry.register(entry.getKey(), (itemStack, world, livingEntity, i) -> { // Registers Identifier key
                // If Null or not whitelisted, return 0 or else return 1
                return currentItemRenderMode == null
                        || !entry.getValue().contains(currentItemRenderMode) ? 0.0F : 1.0F;
            });
        }
    }
}
