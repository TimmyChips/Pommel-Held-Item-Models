package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import java.util.*;

// Todo
//  Will technically parse Identifiers into Strings, not alike Vanilla
//  Example: arrow and minecraft:arrow both work for property, charge_type in the mod.
//  Technically this example does not work in Vanilla, minecraft:arrow throws an error in Vanilla
//  Revisit?

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
                    Case.codec(selfCodec, CodecUtils.IdentifierOrStringCodec.INSTANCE) // Accepts short string, or full id
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

        @Override
        public MapCodec<? extends ItemModelDefinition> getCodec() {
            return codec(ItemModelTypes.CODEC);
        }
    }

    // TODO - case could probably be String object
    /**
     * Renders item models based on a property
     * @param model the model to render "when" a certain property is met
     * @param when the property to match for
     * @param <T> type is either String or Identifier object
     */
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
}
