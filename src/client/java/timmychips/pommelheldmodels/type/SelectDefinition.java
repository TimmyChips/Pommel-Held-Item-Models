package timmychips.pommelheldmodels.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SelectDefinition {
    public record Definition(
            Identifier type,
            List<Case> cases,
            ItemModelDefinition fallback,
            Identifier property,
            @Nullable String block_state_property

    ) implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Case.codec(selfCodec).listOf().fieldOf("cases").forGetter(Definition::cases),
                    selfCodec.fieldOf("fallback").forGetter(Definition::fallback),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property),
                    selfCodec.STRING.optionalFieldOf("block_state_property").forGetter(cd -> Optional.ofNullable(cd.block_state_property()))
            ).apply(instance, (type, cases, fallback, property, optBlockStare) ->
                    new Definition(
                            type, cases, fallback, property,
                            optBlockStare.orElse(null)
            )));
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
}
