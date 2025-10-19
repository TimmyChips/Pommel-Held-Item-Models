package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.property.helper.PommelIdMapper;

public class ItemModelTypes {
    public static final PommelIdMapper ID_MAPPER = new PommelIdMapper();
    public static final Codec<ItemModelDefinition> CODEC = Codec.lazyInitialized(() -> ID_MAPPER.getCodec(Identifier.CODEC));

    static {
        // Register all items model types
        ID_MAPPER.put(Identifier.of("minecraft:select"), SelectDefinition.Definition.codec(CODEC));
        ID_MAPPER.put(Identifier.of("minecraft:condition"), ConditionDefinition.codec(CODEC));
        ID_MAPPER.put(Identifier.of("minecraft:range_dispatch"), RangeDispatchDefinition.Definition.codec(CODEC));
        ID_MAPPER.put(Identifier.of("minecraft:composite"), CompositeModelDefinition.CODEC);
        ID_MAPPER.put(Identifier.of("minecraft:empty"), EmptyModelDefinition.CODEC);
        ID_MAPPER.put(Identifier.of("minecraft:model"), ModelDefinition.CODEC);
    }
}
