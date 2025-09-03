package timmychips.pommelheldmodels.property.type;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.property.helper.JsonElementHelper;
import timmychips.pommelheldmodels.mixin.client.KeyBindingAccessor;

import java.util.Optional;


public record ConditionDefinition(
        Identifier type,
        Identifier property,
        @Nullable String predicate,       // for property "component"
        @Nullable JsonElement value,      //
        @Nullable String component,       // for property "has_component"
        Boolean ignore_default, //
        KeyBinding keybind,
        @Nullable Identifier submergedFluid,
        ItemModelDefinition on_true,
        ItemModelDefinition on_false

) implements ItemModelDefinition {

    public static final Codec<KeyBinding> KEYBIND_CODEC;

    static {
        KEYBIND_CODEC = Codec.STRING.comapFlatMap((id) -> {                      // Keybind string
            KeyBinding keyBinding = KeyBindingAccessor.getKeyIds().get(id);
            return keyBinding != null ? DataResult.success(keyBinding) : DataResult.error(() -> "Invalid keybind: " + id);
        }, KeyBinding::getTranslationKey);
    }

    // TODO
    //  Work on sending packet of data to server:
    //    -> Fetch fix of decoding packets with complex data from enchanted_books, bottles, etc. from other branch

    public static MapCodec<ConditionDefinition> codec(Codec<ItemModelDefinition> selfCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("type").forGetter(ConditionDefinition::type),
                Identifier.CODEC.fieldOf("property").forGetter(ConditionDefinition::property),
                Codec.STRING.optionalFieldOf("predicate").forGetter(cd -> Optional.ofNullable(cd.predicate())),
                JsonElementHelper.JSON_ELEMENT_CODEC.optionalFieldOf("value").forGetter(cd -> Optional.ofNullable(cd.value())),
                Codec.STRING.optionalFieldOf("component").forGetter(cd -> Optional.ofNullable(cd.component())),
                Codec.BOOL.optionalFieldOf("ignore_default").forGetter(cd -> Optional.ofNullable(cd.ignore_default())),
                KEYBIND_CODEC.optionalFieldOf("keybind").forGetter(cd -> Optional.ofNullable(cd.keybind())),
                Identifier.CODEC.optionalFieldOf("fluid").forGetter(cd -> Optional.ofNullable(cd.submergedFluid)),
                selfCodec.fieldOf("on_true").forGetter(ConditionDefinition::on_true),
                selfCodec.fieldOf("on_false").forGetter(ConditionDefinition::on_false)

        ).apply(instance, (type, property, optPredicate, optValue, optComponent, optIgnoreDef, optKeybind, optSubmergedFluid, onTrue, onFalse) ->
                new ConditionDefinition(
                        type, property,
                        optPredicate.orElse(null), optValue.orElse(null),
                        optComponent.orElse(null), optIgnoreDef.orElse(false),
                        optKeybind.orElse(null),
                        optSubmergedFluid.orElse(Identifier.of("minecraft:water")),
                        onTrue, onFalse)
        ));
    }

    @Override
    public MapCodec<? extends ItemModelDefinition> getCodec() {
        return codec(ItemModelTypes.CODEC);
    }
}
