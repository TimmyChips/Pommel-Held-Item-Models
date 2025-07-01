package timmychips.pommelheldmodels.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public class SelectDefinition {
    public record Definition(Identifier type, List<Case> cases, ItemModelDefinition fallback, Identifier property)
            implements ItemModelDefinition {

        public static MapCodec<Definition> codec(Codec<ItemModelDefinition> selfCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Definition::type),
                    Case.codec(selfCodec).listOf().fieldOf("cases").forGetter(Definition::cases),
                    selfCodec.fieldOf("fallback").forGetter(Definition::fallback),
                    Identifier.CODEC.fieldOf("property").forGetter(Definition::property)
            ).apply(instance, Definition::new));
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
