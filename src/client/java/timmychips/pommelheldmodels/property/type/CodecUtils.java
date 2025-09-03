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
}
