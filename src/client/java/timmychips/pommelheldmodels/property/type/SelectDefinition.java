package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SelectDefinition {
    public record Definition(
            Identifier type,
            List<Case> cases,
            @Nullable ItemModelDefinition fallback,
            Identifier property,
            @Nullable String block_state_property,
            @Nullable String component

    ) implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Case.codec(selfCodec).listOf().fieldOf("cases").forGetter(Definition::cases),
                    selfCodec.optionalFieldOf("fallback").forGetter(range -> Optional.ofNullable(range.fallback)),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property),
                    selfCodec.STRING.optionalFieldOf("block_state_property").forGetter(cd -> Optional.ofNullable(cd.block_state_property())),
                    selfCodec.STRING.optionalFieldOf("component").forGetter(cd -> Optional.ofNullable(cd.component()))
            ).apply(instance, (type, cases, optFallback, property, optBlockStare, optComponent) ->
                    new Definition(
                            type, cases, optFallback.orElse(null), property,
                            optBlockStare.orElse(null),
                            optComponent.orElse(null)
            )));
        }
    }

    public record Case(ItemModelDefinition model, List<String> when) {
        public static Codec<Case> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    selfCodec.fieldOf("model").forGetter(Case::model),
                    CodecUtils.STRING_OR_LIST.fieldOf("when").forGetter(Case::when)
            ).apply(instance, Case::new));
        }
    }
}
