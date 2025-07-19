package timmychips.pommelheldmodels.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.resolver.rangeentry.CompassFloat;

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
                    Codec.FLOAT.fieldOf("scale").forGetter(Definition::scale)
            ).apply(instance, (type, property, entries, fallbackOpt, optCompass, optWobble, scale) ->
                    new Definition(
                            type, property, entries, fallbackOpt.orElse(null),
                            optCompass.orElse(null), optWobble.orElse(null),
                            scale)
            ));
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

    @Nullable CompassConfig compass; // contains target + wobble

    public record CompassConfig(CompassFloat.CompassTarget target, boolean wobble) {
        public static final Codec<CompassConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CompassFloat.CompassTarget.CODEC.fieldOf("target").forGetter(CompassConfig::target),
                Codec.BOOL.optionalFieldOf("wobble", true).forGetter(CompassConfig::wobble)
        ).apply(instance, CompassConfig::new));
    }
}