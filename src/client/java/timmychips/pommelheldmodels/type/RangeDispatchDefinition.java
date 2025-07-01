package timmychips.pommelheldmodels.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class RangeDispatchDefinition {

    public record Definition(
            Identifier type,
            Identifier property,
            List<ThresholdEntry> entries,
            @Nullable ItemModelDefinition fallback,
            float scale
    ) implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property),
                    ThresholdEntry.codec(selfCodec).listOf().fieldOf("entries").forGetter(Definition::entries),
                    selfCodec.optionalFieldOf("fallback").forGetter(range -> Optional.ofNullable(range.fallback)),
                    Codec.FLOAT.fieldOf("scale").forGetter(Definition::scale)
            ).apply(instance, (type, property, entries, fallbackOpt, scale) ->
                    new Definition(type, property, entries, fallbackOpt.orElse(null), scale)
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
}