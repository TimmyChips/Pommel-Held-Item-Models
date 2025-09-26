package timmychips.pommelheldmodels.property.resolver;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.ItemModelRegistry;
import timmychips.pommelheldmodels.property.type.ItemModelDefinition;

import java.util.Optional;

    public class ItemModelResolver {
        public static Optional<BakedModel> resolveModel(Identifier itemId, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
            ItemModelDefinition def = ItemModelRegistry.get(itemId);
//            if (def == null) return Optional.ofNullable(ResolveRecursive.getMissingModel());
            if (def == null) return Optional.empty();

            return ResolveRecursive.resolve(def, renderMode, stack, entity);
        }
    }
