package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;

    // 
    private static final String namespace = "pommel";
    private static final String render_held = "is_held";
    private static final String render_offhand = "is_offhand";
    private static final String render_fixed = "is_fixed";
    private static final String render_ground = "is_ground";
    private static final String render_head = "is_head";

    private static final List<ModelTransformationMode> renderModeHands = Arrays.asList(
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
            ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
            ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
    );

    private static final Logger LOGGER = LogUtils.getLogger();

    // 
    private static HashMap<Identifier, List<ModelTransformationMode>> renderTypeWhitelist;
    public static boolean itemInOffhand = false;

    public static void registerHeldModelPredicate() {

        // Creates association to render type and transformation modes
        // HashMap contains Indentifiers (held, on ground) with several mode types linked to each identifier
        renderTypeWhitelist = new HashMap<Identifier, List<ModelTransformationMode>>() {{
            put(Identifier.of(namespace, render_held), renderModeHands ); // Held render modes

            put(Identifier.of(namespace, render_offhand), renderModeHands ); // Held render modes for offhand

            put(Identifier.of(namespace, render_fixed), Arrays.asList( // Item Frame, Fixed render mode
                    ModelTransformationMode.FIXED));

            put(Identifier.of(namespace, render_ground), Arrays.asList( // Thrown on ground render mod
                    ModelTransformationMode.GROUND));

            put(Identifier.of(namespace, render_head), Arrays.asList( // When worn on head armor slot
                    ModelTransformationMode.HEAD));
        }};

        for (var entry:renderTypeWhitelist.entrySet()) { // Performs for each key-value pair
                                                         // Performs for each Identifier and associated List items

            ModelPredicateProviderRegistry.register(entry.getKey(), (itemStack, world, livingEntity, seed) -> {
//                if (currentItemRenderMode == null) return 0.0F;

//                boolean isOffhandPredicate = entry.getKey().getPath().equals(render_offhand);
                boolean isHeldPredicate = entry.getKey().getPath().equals(render_held);

//                LOGGER.info(String.valueOf(entry.getKey().getPath().equals(Identifier.of(namespace, render_head)));
//                LOGGER.info("Comparing against: " + render_offhand);
//                LOGGER.info("getPath(): " + entry.getKey().getPath());

//                LOGGER.info(String.valueOf(renderTypeWhitelist));

//                if (isOffhandPredicate) {
//                    return (itemInOffhand && entry.getValue().contains(currentItemRenderMode)) ? 1.0F : 0.0F;
//                }
//
//                if (isHeldPredicate) {
//                    return (!itemInOffhand && renderTypeWhitelist.get(entry.getKey()).contains(currentItemRenderMode)) ? 1.0F : 0.0F;
//                }

//                if (itemInOffhand && renderTypeWhitelist.containsKey("pommel:is_offhand")) return renderTypeWhitelist.get(entry ? 1.0F : 0.0F;
////                LOGGER.info(String.valueOf(itemInOffhand && renderTypeWhitelist.containsKey(Identifier.of(namespace, render_offhand))));

//     WORKING           return itemInOffhand && renderTypeWhitelist.containsKey(Identifier.of(namespace, render_offhand)) && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;


//                boolean isOffhandPredicate = renderTypeWhitelist.get(entry.getKey());
//                LOGGER.info("got offhand predicate name = " + isOffhandPredicate);
//                LOGGER.info(String.valueOf(entry.getKey().equals(renderTypeWhitelist.get(entry.getKey()))));
                LOGGER.info(String.valueOf(entry.getKey()));
                LOGGER.info(String.valueOf(renderTypeWhitelist.containsKey(entry.getKey()) ? 1.0F : 0.0F));
//                return itemInOffhand && renderTypeWhitelist.containsKey(Identifier.of(namespace, render_offhand)) ? 1.0F : 0.0F;
                return itemInOffhand && renderTypeWhitelist.containsKey(entry.getKey()) ? 1.0F : 0.0F;

//                return itemInOffhand && entry.getKey().equals(renderTypeWhitelist.get(entry.getKey())) ? 1.0F : 0.0F;

//                else return !itemInOffhand && entry.getValue().contains(currentItemRenderMode) ? 1.0F : 0.0F;

//                return !itemInOffhand && renderTypeWhitelist.get(entry.getKey()).contains(currentItemRenderMode) ? 1.0F : 0.0F;

//                return renderTypeWhitelist.get(entry.getKey()).contains(currentItemRenderMode) ? 1.0F : 0.0F;
            });
        }
    }
}
