package timmychips.pommelheldmodels.property.type;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import timmychips.pommelheldmodels.ClientInitializer;

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

    public static final Codec<String> IdentifierOrStringAsStringCodec = Codec.STRING.flatXmap(
            str -> {
//                try {
//                    // Try to interpret it as an Identifier
//                    Identifier id = str.contains(":")
//                            ? Identifier.of(str)
//                            : Identifier.of("minecraft", str);
//                    return DataResult.success(id.toString()); // normalized namespace
//                } catch (InvalidIdentifierException e) {
//                    // Not a valid Identifier, keep it as-is
//                    return DataResult.success(str);
//                }
                // Possible to return both string and identifier as string (i.e. "arrow" and "minecraft:arrow") together?
                ClientInitializer.LOGGER.info(str);
                return DataResult.success(str); // only return String
            },
            DataResult::success
    );

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
