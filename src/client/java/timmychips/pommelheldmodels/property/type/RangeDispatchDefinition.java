package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.property.resolver.rangeentry.ClockTimeFloat;
import timmychips.pommelheldmodels.property.resolver.rangeentry.CompassFloat;

import java.util.List;
import java.util.Optional;

public final class RangeDispatchDefinition {

    public record Definition(
            Identifier type,
            Identifier property,
            List<ThresholdEntry> entries,
            @Nullable ItemModelDefinition fallback,
            @Nullable CompassFloat.CompassTarget target,
            @Nullable Boolean wobble,
            @Nullable Boolean countNormalize,
            @Nullable ClockTimeFloat.ClockSource clockSource,
            float usePeriod,
            @Nullable Boolean useRemaining,
            float scale
    ) implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property),
                    ThresholdEntry.codec(selfCodec).listOf().fieldOf("entries").forGetter(Definition::entries),
                    selfCodec.optionalFieldOf("fallback").forGetter(range -> Optional.ofNullable(range.fallback)),
                    CompassFloat.CompassTarget.CODEC.optionalFieldOf("target").forGetter(range -> Optional.ofNullable(range.target)),
                    Codec.BOOL.optionalFieldOf("wobble").forGetter(range -> Optional.ofNullable(range.wobble)),
                    Codec.BOOL.optionalFieldOf("normalize").forGetter(range -> Optional.ofNullable(range.countNormalize)),
                    ClockTimeFloat.ClockSource.CODEC.optionalFieldOf("source").forGetter(range -> Optional.ofNullable(range.clockSource)),
                    Codec.FLOAT.optionalFieldOf("period").forGetter(range -> Optional.of(range.usePeriod)),
                    Codec.BOOL.optionalFieldOf("remaining").forGetter(range -> Optional.ofNullable(range.useRemaining)),
                    Codec.FLOAT.optionalFieldOf("scale").forGetter(range -> Optional.of(range.scale))
            ).apply(instance,
                    (type, property, entries, fallbackOpt, optCompassTarget, optWobble, optCountNormalize, optClockSource, optUsePeriod, optUseRemaining, scale) ->
                    new Definition(
                            type, property, entries, fallbackOpt.orElse(null),
                            optCompassTarget.orElse(null), optWobble.orElse(true),
                            optCountNormalize.orElse(true),
                            optClockSource.orElse(null),
                            optUsePeriod.orElse(1F),
                            optUseRemaining.orElse(false),
                            scale.orElse(1F))
            ));
        }

        @Override
        public MapCodec<? extends ItemModelDefinition> getCodec() {
            return codec(ItemModelTypes.CODEC);
        }
    }

    public record ThresholdEntry(ItemModelDefinition model, float threshold) {
        public static Codec<ThresholdEntry> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    selfCodec.fieldOf("model").forGetter(ThresholdEntry::model),
                    Codec.FLOAT.fieldOf("threshold").forGetter(ThresholdEntry::threshold)
            ).apply(instance, ThresholdEntry::new));
        }
    }
}