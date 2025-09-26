package timmychips.pommelheldmodels.property.type;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.property.resolver.ResolveRecursive;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record CompositeItemModel(List<BakedModel> modelParts) implements BakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return modelParts.stream()
                .flatMap(bakedModel -> {
                        List<BakedQuad> quads = bakedModel.getQuads(state, face, random);
                        return quads != null ? quads.stream() : ResolveRecursive.getMissingModel().getQuads(state, face, random).stream(); // Stream quads if not null; else return empty
                })
                .toList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return modelParts.stream().anyMatch(BakedModel::useAmbientOcclusion);
    }

    @Override
    public boolean hasDepth() {
        return modelParts.stream().anyMatch(BakedModel::hasDepth);
    }

    @Override
    public boolean isSideLit() {
        return modelParts.stream().anyMatch(BakedModel::isSideLit);
    }

    @Override
    public boolean isBuiltin() {
        return modelParts.stream().anyMatch(BakedModel::isBuiltin);
    }

    @Override
    public Sprite getParticleSprite() {
        return modelParts.isEmpty()
                ? MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite()
                : modelParts.getFirst().getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return modelParts.isEmpty()
                ? ModelTransformation.NONE
                : modelParts.getFirst().getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return modelParts.isEmpty()
                ? ModelOverrideList.EMPTY
                : modelParts.getFirst().getOverrides();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        modelParts.getFirst().emitItemQuads(stack, randomSupplier, context);
        modelParts.getLast().emitItemQuads(stack, randomSupplier, context);
        context.popTransform();
    }
}
