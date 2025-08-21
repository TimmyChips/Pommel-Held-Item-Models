package timmychips.pommelheldmodels.property.type;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompositeItemModel implements BakedModel {
    private final List<BakedModel> modelParts;

    public CompositeItemModel(List<BakedModel> modelParts) {
        this.modelParts = modelParts;
    }

    public List<BakedModel> getModelParts() {
        return modelParts;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return modelParts.stream()
                .flatMap(bakedModel -> bakedModel.getQuads(state, face, random).stream())
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
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        // Use first child for particles
        return modelParts.getFirst().getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return modelParts.isEmpty() ? ModelTransformation.NONE : modelParts.getFirst().getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return modelParts.isEmpty() ? ModelOverrideList.EMPTY : modelParts.getFirst().getOverrides();
    }
}
