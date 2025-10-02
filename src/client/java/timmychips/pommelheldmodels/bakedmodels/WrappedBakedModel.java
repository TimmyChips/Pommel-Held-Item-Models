package timmychips.pommelheldmodels.bakedmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import timmychips.pommelheldmodels.property.resolver.ResolveRecursive;
import timmychips.pommelheldmodels.property.type.ItemModelDefinition;
import timmychips.pommelheldmodels.property.type.ModelDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class WrappedBakedModel implements BakedModel {
    private final BakedModel delegate;
    private final ItemModelDefinition definition;

    public WrappedBakedModel(BakedModel delegate, ItemModelDefinition def) {
        this.delegate = delegate;
        this.definition = def;
    }

    public ModelTransformation getPommelTransformations() {
        if (definition instanceof ModelDefinition md) {
            return MinecraftClient.getInstance().getBakedModelManager().getModel(md.model()).getTransformation(); // however you store them
        }
        return ModelTransformation.NONE;
    }

    @Override
    public ModelTransformation getTransformation() {
        return getPommelTransformations();
    }

    // --- delegate everything else ---
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        return delegate.getQuads(state, face, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return delegate.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return delegate.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return delegate.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return delegate.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return delegate.getParticleSprite();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return delegate.getOverrides();
    }
}
