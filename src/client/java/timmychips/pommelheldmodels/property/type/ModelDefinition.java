package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record ModelDefinition(Identifier type, Identifier model) implements ItemModelDefinition {
    public static final MapCodec<ModelDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(ModelDefinition::type),
            Identifier.CODEC.fieldOf("model").forGetter(ModelDefinition::model)
    ).apply(instance, ModelDefinition::new));
}
