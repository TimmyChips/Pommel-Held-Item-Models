package timmychips.relignitemodeldefinition.property.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record EmptyModelDefinition(Identifier type) implements ItemModelDefinition {
    public static final MapCodec<EmptyModelDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(EmptyModelDefinition::type)
    ).apply(instance, EmptyModelDefinition::new));

    @Override
    public MapCodec<? extends ItemModelDefinition> getCodec() {
        return CODEC;
    }
}
