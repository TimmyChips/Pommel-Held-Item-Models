package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

public final class SelectDefinition {
    public record Definition(
            Identifier type,
            List<Case<String>> cases,
            @Nullable ItemModelDefinition fallback,
            Identifier property,
            @Nullable String blockStateProperty,
            boolean chargeIgnoreDefault, boolean chargeIgnoreUnknown, // Custom modded fields for charge_type property
            @Nullable String component

    ) implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Case.codec(selfCodec, Codec.STRING).listOf().fieldOf("cases").forGetter(Definition::cases),
                    selfCodec.optionalFieldOf("fallback").forGetter(range -> Optional.ofNullable(range.fallback)),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property),
                    selfCodec.STRING.optionalFieldOf("block_state_property").forGetter(cd -> Optional.ofNullable(cd.blockStateProperty())),
                    selfCodec.BOOL.optionalFieldOf("ignore_default").forGetter(cd -> Optional.of(cd.chargeIgnoreDefault)),
                    selfCodec.BOOL.optionalFieldOf("ignore_unknown").forGetter(cd -> Optional.of(cd.chargeIgnoreUnknown)),
                    selfCodec.STRING.optionalFieldOf("component").forGetter(cd -> Optional.ofNullable(cd.component()))
            ).apply(instance, (type, cases, optFallback, property, optBlockState, optChargeIgnoreDefault, optChargeIgnoreUnknown, optComponent) ->
                    new Definition(
                            type, cases, optFallback.orElse(null), property,
                            optBlockState.orElse(null),
                            optChargeIgnoreDefault.orElse(false), optChargeIgnoreUnknown.orElse(false),
                            optComponent.orElse(null)
            )));
        }
    }

    public record Case<T>(ItemModelDefinition model, HashSet<T> when) {
        public static <T> Codec<Case<T>> codec(
                Codec<ItemModelDefinition> selfCodec,
                Codec<T> valueCodec
        ) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    selfCodec.fieldOf("model").forGetter((Case<T> c) -> c.model),
                    CodecUtils.ofValueOrList(valueCodec).xmap(
                            HashSet::new,
                            ArrayList::new
                    ).fieldOf("when").forGetter((Case<T> c) -> c.when)
            ).apply(instance, Case::new));
        }
    }


}
