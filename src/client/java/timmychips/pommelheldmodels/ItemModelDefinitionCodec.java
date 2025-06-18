package timmychips.pommelheldmodels;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ItemModelDefinitionCodec {

    public static final Codec<ItemModelDefinition> DEFINITION_CODEC = Codec.either(
            SelectDefinition.MAP_CODEC.codec(), // <- turn MapCodec into Codec
            ModelDefinition.MAP_CODEC.codec()
    ).xmap(
            either -> either.map(l -> l, r -> r),
            def -> def instanceof SelectDefinition s ? Either.left(s) : Either.right((ModelDefinition) def)
    );


    public sealed interface ItemModelDefinition permits ModelDefinition, SelectDefinition {}

    public record ModelDefinition(String type, Identifier model) implements ItemModelDefinition {
        public static final MapCodec<ModelDefinition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(ModelDefinition::type),
                Identifier.CODEC.fieldOf("model").forGetter(ModelDefinition::model)
        ).apply(instance, ModelDefinition::new));

        public static final Codec<ModelDefinition> CODEC = MAP_CODEC.codec();
    }

    public record SelectDefinition(String type, List<Case> cases, ModelDefinition fallback, String property) implements ItemModelDefinition {
        public static final MapCodec<SelectDefinition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(SelectDefinition::type),
                Case.CODEC.listOf().fieldOf("cases").forGetter(SelectDefinition::cases),
                ModelDefinition.CODEC.fieldOf("fallback").forGetter(SelectDefinition::fallback),
                Codec.STRING.fieldOf("property").forGetter(SelectDefinition::property)
        ).apply(instance, SelectDefinition::new));

        public static final Codec<SelectDefinition> CODEC = MAP_CODEC.codec();
    }

    public record Case(ModelDefinition model, List<String> when) {
        public static final Codec<Case> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ModelDefinition.CODEC.fieldOf("model").forGetter(Case::model),
                Codec.STRING.listOf().fieldOf("when").forGetter(Case::when)
        ).apply(instance, Case::new));
    }
}
