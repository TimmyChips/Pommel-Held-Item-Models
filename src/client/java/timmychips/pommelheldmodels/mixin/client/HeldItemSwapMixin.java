package timmychips.pommelheldmodels.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.ItemModelRegistry;
import timmychips.pommelheldmodels.property.type.ItemModelRootDefinition;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemSwapMixin {
    @Shadow
    private ItemStack mainHand;

    @Shadow
    private ItemStack offHand;

    @Shadow
    protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    /*
    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"))
    private void armHoldingItemOverrideSwap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
        float newEquipProgress = equipProgress;

//        if (mainHand != null) {
//            Identifier handItemId = mainHand.isEmpty() ? Registries.ITEM.getId(offHand.getItem()) : Registries.ITEM.getId(mainHand.getItem());
//            ItemModelRootDefinition handDef = ItemModelRegistry.getRoot(handItemId);
//
//            boolean itemHandAnimationSwap = true;
//            if (handDef != null) itemHandAnimationSwap = handDef.handAnimationSwap();
//
//            if (!itemHandAnimationSwap) newEquipProgress = 1F;
//        }

        newEquipProgress = 1F;

        HeldItemRenderer self = (HeldItemRenderer)(Object)this;
        self.renderArmHoldingItem(matrices, vertexConsumers, light, newEquipProgress, swingProgress, arm);
        ci.cancel();
    }

     */

    @Shadow
    private float equipProgressOffHand;

    @Shadow
    private float equipProgressMainHand;

    @ModifyArg(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"
            ),
            index = 3 // equipProgress
    )
    private float disableEmptyHandEquipProgress(float equipProgress) {
        return 0.0F;
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
        return 0.0F;
    }
}
