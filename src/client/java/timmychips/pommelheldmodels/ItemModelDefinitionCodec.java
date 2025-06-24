package timmychips.pommelheldmodels;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ItemModelDefinitionCodec {

    public static Codec<ItemModelDefinition> DEFINITION_CODEC = null;

    static {
        // Lazy to allow recursion
        DEFINITION_CODEC = Codec.lazyInitialized(() ->
            Codec.either(
                SelectDefinition.codec(DEFINITION_CODEC).codec(),
                Codec.either(
                    ConditionDefinition.codec(DEFINITION_CODEC).codec(),
                    Codec.either(
                            RangeDispatchDefinition.codec(DEFINITION_CODEC).codec(),
                            ModelDefinition.MAP_CODEC.codec()
                    )
                )
            ).xmap(
                either -> either.map(
                    select -> select,
                    inner -> inner.map(
                            condition -> condition,
                            inner2 -> inner2.map(range -> range, model -> model)
                    )
                ),
                def -> {
                    if (def instanceof SelectDefinition s) return Either.left(s);
                    if (def instanceof ConditionDefinition c) return Either.right(Either.left(c));
                    if (def instanceof RangeDispatchDefinition r) return Either.right(Either.right(Either.left(r)));
                    if (def instanceof ModelDefinition m) return Either.right(Either.right(Either.right(m)));
                    throw new IllegalStateException("Unknown ItemModelDefinition: " + def);
                }
            )
        );

    }

    public sealed interface ItemModelDefinition permits ConditionDefinition, ModelDefinition, RangeDispatchDefinition, SelectDefinition {}

    public record ModelDefinition(String type, Identifier model) implements ItemModelDefinition {
        public static final MapCodec<ModelDefinition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(ModelDefinition::type),
                Identifier.CODEC.fieldOf("model").forGetter(ModelDefinition::model)
        ).apply(instance, ModelDefinition::new));
    }

    public record SelectDefinition(String type, List<Case> cases, ItemModelDefinition fallback, String property)
            implements ItemModelDefinition {
        public static MapCodec<SelectDefinition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("type").forGetter(SelectDefinition::type),
                    Case.codec(selfCodec).listOf().fieldOf("cases").forGetter(SelectDefinition::cases),
                    selfCodec.fieldOf("fallback").forGetter(SelectDefinition::fallback),
                    Codec.STRING.fieldOf("property").forGetter(SelectDefinition::property)
            ).apply(instance, SelectDefinition::new));
        }
    }

    public record ConditionDefinition(String type, String property, ItemModelDefinition on_true, ItemModelDefinition on_false)
            implements ItemModelDefinition {
        public static MapCodec<ConditionDefinition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("type").forGetter(ConditionDefinition::type),
                    Codec.STRING.fieldOf("property").forGetter(ConditionDefinition::property),
                    selfCodec.fieldOf("on_true").forGetter(ConditionDefinition::on_true),
                    selfCodec.fieldOf("on_false").forGetter(ConditionDefinition::on_false)
            ).apply(instance, ConditionDefinition::new));
        }
    }

    public record Case(ItemModelDefinition model, List<String> when) {
        public static Codec<Case> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    selfCodec.fieldOf("model").forGetter(Case::model),
                    Codec.STRING.listOf().fieldOf("when").forGetter(Case::when)
            ).apply(instance, Case::new));
        }
    }

    public record RangeDispatchDefinition(
            String type,
            String property,
            List<ThresholdEntry> entries,
            ItemModelDefinition fallback,
            float scale
    ) implements ItemModelDefinition {
        public static MapCodec<RangeDispatchDefinition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("type").forGetter(RangeDispatchDefinition::type),
                    Codec.STRING.fieldOf("property").forGetter(RangeDispatchDefinition::property),
                    ThresholdEntry.codec(selfCodec).listOf().fieldOf("entries").forGetter(RangeDispatchDefinition::entries),
                    selfCodec.fieldOf("fallback").forGetter(RangeDispatchDefinition::fallback),
                    Codec.FLOAT.fieldOf("scale").forGetter(RangeDispatchDefinition::scale)
            ).apply(instance, RangeDispatchDefinition::new));
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
