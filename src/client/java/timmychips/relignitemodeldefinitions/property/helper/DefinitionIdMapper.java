package timmychips.relignitemodeldefinitions.property.helper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinitions.property.type.codec.ItemModelDefinition;

import java.util.Objects;

public class DefinitionIdMapper {
    private final BiMap<Identifier, MapCodec<? extends ItemModelDefinition>> idToCodec = HashBiMap.create();

    public Codec<ItemModelDefinition> getCodec(Codec<Identifier> idCodec) {
        return idCodec.dispatch(
                // from definition -> id
                def -> {
                    MapCodec<? extends ItemModelDefinition> codec = def.getCodec();
                    Identifier id = idToCodec.inverse().get(codec);
                    if (id == null) {
                        throw new IllegalStateException("Unknown definition codec: " + codec);
                    }
                    return id;
                },
                // from id -> codec
                id -> {
                    MapCodec<? extends ItemModelDefinition> codec = idToCodec.get(id);
                    if (codec == null) {
                        throw new IllegalStateException("Unknown codec id: " + id);
                    }
                    return codec;
                }
        );
    }

    public void put(Identifier id, MapCodec<? extends ItemModelDefinition> value) {
        Objects.requireNonNull(value, () -> "Value for " + id + " is null");
        idToCodec.put(id, value);
    }
}
