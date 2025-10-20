package timmychips.pommelheldmodels.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.ItemModelRegistry;
import timmychips.pommelheldmodels.property.type.ItemModelDefinition;
import timmychips.pommelheldmodels.property.type.ItemModelRootDefinition;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemSwapMixin {

    @Unique
    private float doModelHandSwap(float equipProgress) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null) ClientInitializer.LOGGER.info("Player is null");
        if (clientPlayer != null) {
            ItemStack heldItem = !clientPlayer.getMainHandStack().isEmpty() ? clientPlayer.getMainHandStack() : clientPlayer.getOffHandStack();
            Identifier heldId = Registries.ITEM.getId(heldItem.getItem());
            ItemModelRootDefinition def = ItemModelRegistry.getRoot(heldId);

            String strDef = null;
            String strDefHandAnim = null;
            if (def != null) {
                strDef = String.valueOf(def);
                strDefHandAnim = String.valueOf(def.handAnimationSwap());
            }
            ClientInitializer.LOGGER.info("Held item: {} Held item id: {} Root Definition: {} Hand Animation Swap for Model: {}", heldItem, heldId, strDef, strDefHandAnim);

            if (def != null && !def.handAnimationSwap()) return 0F; // Disable hand animation swap if current held item model has hand swap set to false
        }
        return equipProgress;
    }

    @ModifyArg(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"
            ),
            index = 3 // equipProgress
    )
    private float disableEmptyHandEquipProgress(float equipProgress) {
        return doModelHandSwap(equipProgress);
    }

    @ModifyArg(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"
            ),
            index = 2 // the equipProgress parameter
    )
    private float disableItemEquipProgress(float equipProgress) {
        return doModelHandSwap(equipProgress);
    }
}
