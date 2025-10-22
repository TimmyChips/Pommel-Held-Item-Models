package timmychips.relignitemodeldefinition.property.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public record CompositeModelDefinition(Identifier type, List<ItemModelDefinition> models) implements ItemModelDefinition {
    public static final MapCodec<CompositeModelDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(CompositeModelDefinition::type),
            ItemModelTypes.CODEC.listOf().fieldOf("models").forGetter(CompositeModelDefinition::models)
    ).apply(instance, CompositeModelDefinition::new));

    @Override
    public MapCodec<? extends ItemModelDefinition> getCodec() {
        return CODEC;
    }
}
