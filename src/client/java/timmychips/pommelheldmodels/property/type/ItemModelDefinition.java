package timmychips.pommelheldmodels.property.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Identifier;

import java.util.Map;

// Entry point to item model definition types
public sealed interface ItemModelDefinition
        permits CompositeModelDefinition, ConditionDefinition, EmptyModelDefinition, ModelDefinition, RangeDispatchDefinition.Definition, SelectDefinition.Definition {

    /**
     * Every subtype must return its own codec.
     */
    MapCodec<? extends ItemModelDefinition> getCodec();
}
