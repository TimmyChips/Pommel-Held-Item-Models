package timmychips.pommelheldmodels.mixin.client;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Removes the rotation lock to 22.5 and 45 degree multiples
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.render.model.json.ModelElement$Deserializer")
public abstract class UnlockedModelRotationDeserializerMixin implements JsonDeserializer<ModelElement> {
    @Inject(method = "deserializeRotationAngle(Lcom/google/gson/JsonObject;)F", at = @At("HEAD"), cancellable = true)
    private void unlockedRotationAngle(JsonObject object, CallbackInfoReturnable<Float> cir) {
        float unlockedAngle = JsonHelper.getFloat(object, "angle");
        cir.setReturnValue(unlockedAngle);
    }
}
