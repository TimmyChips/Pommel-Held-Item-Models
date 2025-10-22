package timmychips.relignitemodeldefinitions.bakedmodels;

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
import timmychips.relignitemodeldefinitions.property.resolver.ResolveRecursive;
import timmychips.relignitemodeldefinitions.property.type.codec.ItemModelDefinition;

import java.util.List;
import java.util.function.Supplier;

public class CompositeItemModel implements BakedModel {

    private final List<ItemModelDefinition> modelParts;
    private final ModelTransformationMode renderMode;
    private final ItemStack stack;
    private final LivingEntity entity;

    public CompositeItemModel(List<ItemModelDefinition> modelDefinitions, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) {
        this.modelParts = modelDefinitions;
        this.renderMode = renderMode;
        this.stack = stack;
        this.entity = entity;
    }

    // Return list of models from this instance
    public List<BakedModel> getModels() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .toList();
    }

    // Get quads for each model part
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
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

    static Transformation extractTransformation(BakedModel model, ModelTransformationMode mode) {
        if (model != null) {
            ModelTransformation modelTransformation = model.getTransformation();
            if (modelTransformation != null) {
                Transformation transformation = modelTransformation.getTransformation(mode);
                if (transformation != Transformation.IDENTITY) {
                    return transformation;
                }
            }
        }

        return Transformation.IDENTITY;
    }

    static ModelTransformation copyTransformations(BakedModel part) {
        Transformation transformation = extractTransformation(part, ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = extractTransformation(part, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = extractTransformation(part, ModelTransformationMode.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = extractTransformation(part, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = extractTransformation(part, ModelTransformationMode.HEAD);
        Transformation transformation6 = extractTransformation(part, ModelTransformationMode.GUI);
        Transformation transformation7 = extractTransformation(part, ModelTransformationMode.GROUND);
        Transformation transformation8 = extractTransformation(part, ModelTransformationMode.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    // TODO only gets transformations from the first model, need to do per-part transformations
    @Override
    public ModelTransformation getTransformation() {
        List<BakedModel> children = modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .toList();

        for (BakedModel part : children) {
//            return copyTransformations(part);
            return part.getTransformation();
        }
        return ModelTransformation.NONE;
    }

    // Get item overrides; doesn't matter though since we're replacing the item override system
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
    public boolean isVanillaAdapter() {
        return false; // False to trigger FabricBakedModel rendering
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        List<BakedModel> bakedModels = modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .toList();

        modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .forEach(baked -> baked.emitItemQuads(stack, randomSupplier, context));

        // Emit all quads
//        for (BakedModel part : bakedModels) {
//            part.emitItemQuads(stack, randomSupplier, context);
//        }
    }
}
