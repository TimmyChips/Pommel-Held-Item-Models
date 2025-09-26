package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.property.resolver.ResolveRecursive;

import java.util.List;

public record CompositeModelDefinition(Identifier type, List<Identifier> models) implements ItemModelDefinition {
    public static final MapCodec<CompositeModelDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(CompositeModelDefinition::type),
            Identifier.CODEC
                    .listOf()
                    .fieldOf("models")
                    .forGetter(CompositeModelDefinition::models)
    ).apply(instance, CompositeModelDefinition::new));

    @Override
    public MapCodec<? extends ItemModelDefinition> getCodec() {
        return CODEC;
    }

    public BakedModel bake(FabricBakedModelManager manager) {
//        List<BakedModel> bakedParts = models.stream()
//                .map(manager::getModel)
//                .toList();
//        CompositeItemModel compositeItemModel = new CompositeItemModel(bakedParts);
//        if (compositeItemModel != null) return compositeItemModel;
        return ResolveRecursive.getMissingModel();
    }
}
