package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public final class SelectDefinition {
    public record Definition(
            Identifier type,
            List<Case<Identifier>> cases, // Now strictly typed
            @Nullable ItemModelDefinition fallback,
            Identifier property,
            @Nullable String blockStateProperty,
            boolean chargeIgnoreDefault,
            boolean chargeIgnoreUnknown,
            @Nullable String component
    ) implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Case.codec(selfCodec, IdentifierOrStringCodec.INSTANCE) // Now supports short + full IDs
                            .listOf()
                            .fieldOf("cases")
                            .forGetter(Definition::cases),
                    selfCodec.optionalFieldOf("fallback").forGetter(d -> Optional.ofNullable(d.fallback)),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property),
                    Codec.STRING.optionalFieldOf("block_state_property").forGetter(d -> Optional.ofNullable(d.blockStateProperty)),
                    Codec.BOOL.optionalFieldOf("ignore_default").forGetter(d -> Optional.of(d.chargeIgnoreDefault)),
                    Codec.BOOL.optionalFieldOf("ignore_unknown").forGetter(d -> Optional.of(d.chargeIgnoreUnknown)),
                    Codec.STRING.optionalFieldOf("component").forGetter(d -> Optional.ofNullable(d.component))
            ).apply(instance, (type, cases, optFallback, property, optBlockState, optChargeIgnoreDefault, optChargeIgnoreUnknown, optComponent) ->
                    new Definition(
                            type, cases,
                            optFallback.orElse(null), property,
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
                    CodecUtils.ofValueOrList(valueCodec)
                            .xmap(HashSet::new, ArrayList::new)
                            .fieldOf("when")
                            .forGetter((Case<T> c) -> c.when)
            ).apply(instance, Case::new));
        }
    }

    /**
     * Custom Codec that accepts either short form ("arrow") or full form ("minecraft:arrow")
     * and always converts to an Identifier with a namespace.
     */
    public static final class IdentifierOrStringCodec {
        public static final Codec<Identifier> INSTANCE = Codec.STRING.xmap(
                str -> {
                    if (!str.contains(":")) {
                        return Identifier.of("minecraft", str);
                    }
                    return Identifier.of(str);
                },
                Identifier::toString
        );
    }
}
