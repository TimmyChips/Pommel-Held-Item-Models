package timmychips.pommelheldmodels.property.type;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.property.resolver.ResolveRecursive;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record CompositeItemModel(List<ItemModelDefinition> modelParts, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) implements BakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        // You’ll need renderMode + stack + entity passed somehow; maybe wrap ResolveRecursive here
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .flatMap(baked -> baked.getQuads(state, face, random).stream())
                .toList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::useAmbientOcclusion);
    }

    @Override
    public boolean hasDepth() {
        return modelParts.stream().map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::hasDepth);
    }

    @Override
    public boolean isSideLit() {
        return modelParts.stream().map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::isSideLit);
    }

    @Override
    public boolean isBuiltin() {
        return modelParts.stream().map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::isBuiltin);
    }

    @Override
    public Sprite getParticleSprite() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .findFirst() // pick the first resolved model
                .map(BakedModel::getParticleSprite)
                .orElse(MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite());
    }

    // TODO only gets transformations from the first model, need to do per-part transformations
    @Override
    public ModelTransformation getTransformation() {
        List<BakedModel> childTransforms = modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .toList();

        if (childTransforms.isEmpty()) {
            return ModelTransformation.NONE;
        }

        childTransforms.getFirst().getTransformation();
//        // Helper: pick the first non-identity transform across all children
//        java.util.function.Function<ModelTransformationMode, Transformation> merge = mode -> {
//            Transformation result = Transformation.IDENTITY;
//            for (BakedModel baked : childTransforms) {
//                Transformation next = baked.getTransformation().getTransformation(mode);
//                if (next != Transformation.IDENTITY) {
//                    result = result.compose(next);
//                    // or next.compose(result) depending on desired order
//                }
//            }
//            return result;
//        };

        return childTransforms.getFirst().getTransformation();

//        return new ModelTransformation(
//                pick.apply(ModelTransformationMode.THIRD_PERSON_LEFT_HAND),
//                pick.apply(ModelTransformationMode.THIRD_PERSON_RIGHT_HAND),
//                pick.apply(ModelTransformationMode.FIRST_PERSON_LEFT_HAND),
//                pick.apply(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND),
//                pick.apply(ModelTransformationMode.HEAD),
//                pick.apply(ModelTransformationMode.GUI),
//                pick.apply(ModelTransformationMode.GROUND),
//                pick.apply(ModelTransformationMode.FIXED)
//        );
    }

    @Override
    public ModelOverrideList getOverrides() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .findFirst()
                .map(BakedModel::getOverrides)
                .orElse(ModelOverrideList.EMPTY);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .forEach(resolved -> resolved.emitItemQuads(stack, randomSupplier, context));
    }
}
