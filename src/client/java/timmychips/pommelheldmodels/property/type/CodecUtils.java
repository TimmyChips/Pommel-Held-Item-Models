package timmychips.pommelheldmodels.property.type;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CodecUtils {
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

    public static <T> Codec<List<T>> ofValueOrList(Codec<T> valueCodec) {
        return Codec.either(
                valueCodec,
                valueCodec.listOf()
        ).xmap(
                either -> either.map(List::of, list -> list),
                list -> list.size() == 1
                        ? com.mojang.datafixers.util.Either.left(list.getFirst())
                        : com.mojang.datafixers.util.Either.right(list)
        );
    }

    public static class IdMapper<I, V> {
        private final BiMap<I, V> values = HashBiMap.create();

        public Codec getCodec(Codec<I> idCodec) {
            BiMap<V, I> biMap = this.values.inverse();
            BiMap<I, V> var10001 = this.values;
            Objects.requireNonNull(var10001);
            Function var3 = var10001::get;
            Objects.requireNonNull(biMap);
            return idChecked(idCodec, var3, biMap::get);
        }

        public IdMapper<I, V> put(I id, V value) {
            Objects.requireNonNull(value, () -> "Value for " + String.valueOf(id) + " is null");
            this.values.put(id, value);
            return this;
        }

        public static <I, E> Codec<E> idChecked(Codec<I> idCodec, Function<I, E> idToElement, Function<E, I> elementToId) {
            return idCodec.flatXmap((id) -> {
                E object = (E)idToElement.apply(id);
                return object == null ? DataResult.error(() -> "Unknown element id: " + String.valueOf(id)) : DataResult.success(object);
            }, (element) -> {
                I object = (I)elementToId.apply(element);
                return object == null ? DataResult.error(() -> "Element with unknown id: " + String.valueOf(element)) : DataResult.success(object);
            });
        }
    }
}
