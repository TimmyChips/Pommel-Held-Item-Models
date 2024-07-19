package timmychips.pommelheldmodels;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;

public class HeldItemPredicate {
    public static ModelTransformationMode currentItemRenderMode;

    public static void registerHeldModelPredicate() {
        // Registers "pommel:is_held" item predicate for use in item models
        ModelPredicateProviderRegistry.register(Identifier.of("pommel", "is_held"), (itemStack, world, livingEntity, i) -> {

            // Returns is_held item predicate to 0.0F, so it'll render the standard item model
            if (currentItemRenderMode == null) {
                return 0.0F;
            }

            // Returns is_held item predicate for model
                // 0.0F is rendered when in GUI, on ground, or fixed (in item frame)
                // 1.0F is rendered when it's held in hand
            return currentItemRenderMode == ModelTransformationMode.GUI
                    || currentItemRenderMode == ModelTransformationMode.GROUND
                    || currentItemRenderMode == ModelTransformationMode.FIXED ? 0.0F : 1.0F;
        });
    }
}
