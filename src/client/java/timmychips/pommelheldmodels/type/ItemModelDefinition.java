package timmychips.pommelheldmodels.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

// Entry point to item model definition types
public sealed interface ItemModelDefinition
        permits ConditionDefinition, ModelDefinition, RangeDispatchDefinition.Definition, SelectDefinition.Definition {

    Codec<ItemModelDefinition> CODEC = createCodec();

    private static Codec<ItemModelDefinition> createCodec() {
        return Codec.lazyInitialized(() -> Codec.either(
                SelectDefinition.Definition.codec(CODEC).codec(),
                Codec.either(
                        ConditionDefinition.codec(CODEC).codec(),
                        Codec.either(
                                RangeDispatchDefinition.Definition.codec(CODEC).codec(),
                                ModelDefinition.CODEC.codec()
                        )
                )
        ).xmap(
                either -> either.map(
                        select -> select,
                        inner -> inner.map(
                                cond -> cond,
                                deeper -> deeper.map(range -> range, model -> model)
                        )
                ),
                def -> {
                    if (def instanceof SelectDefinition.Definition s) return Either.left(s);
                    if (def instanceof ConditionDefinition c) return Either.right(Either.left(c));
                    if (def instanceof RangeDispatchDefinition.Definition r)
                        return Either.right(Either.right(Either.left(r)));
                    if (def instanceof ModelDefinition m) return Either.right(Either.right(Either.right(m)));
                    throw new IllegalStateException("Unknown ItemModelDefinition: " + def);
                }
        ));
    }
}
