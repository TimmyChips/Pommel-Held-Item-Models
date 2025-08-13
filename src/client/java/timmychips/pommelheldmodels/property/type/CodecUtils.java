package timmychips.pommelheldmodels.property.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.List;

public class CodecUtils {
    // Accepts either a string or a list of strings
    public static final Codec<List<String>> STRING_OR_LIST = Codec.either(
            Codec.STRING, Codec.STRING.listOf()
    ).xmap(
            either -> either.map(List::of, list -> list),
            list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list)
    );

    public static <T> Codec<List<T>> ofValueOrList(Codec<T> valueCodec) {
        return Codec.either(
                valueCodec,
                valueCodec.listOf()
        ).xmap(
                either -> either.map(List::of, list -> list),
                list -> list.size() == 1
                        ? com.mojang.datafixers.util.Either.left(list.get(0))
                        : com.mojang.datafixers.util.Either.right(list)
        );
    }
}
