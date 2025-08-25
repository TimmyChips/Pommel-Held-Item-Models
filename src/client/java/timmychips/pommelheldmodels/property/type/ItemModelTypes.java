package timmychips.pommelheldmodels.property.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ItemModelTypes {
    public static final CodecUtils.IdMapper<Identifier, MapCodec<? extends ItemModelDefinition>> DEFINITIONS =
            new CodecUtils.IdMapper<>();
    public static final Codec<ItemModelDefinition> CODEC;

    public static void bootstrap() {
        DEFINITIONS.put(Identifier.of("pommel", "select"), SelectDefinition.Definition.codec(CODEC));
        DEFINITIONS.put(Identifier.of("pommel", "condition"), ConditionDefinition.codec(CODEC));
        DEFINITIONS.put(Identifier.of("pommel", "range_dispatch"), RangeDispatchDefinition.Definition.codec(CODEC));
        // … add more as you define them
    }

    static {
        CODEC = DEFINITIONS.getCodec(Identifier.CODEC).dispatch(ItemModelDefinition::getCodec, (codec -> codec));
    }
}
