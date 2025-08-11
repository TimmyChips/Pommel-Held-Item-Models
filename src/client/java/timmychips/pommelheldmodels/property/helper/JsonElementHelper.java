package timmychips.pommelheldmodels.property.helper;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

public class JsonElementHelper {
    public static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
            dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(), // to JsonElement
            json -> new Dynamic<>(JsonOps.INSTANCE, json) // from JsonElement
    );
}
