package timmychips.pommelheldmodels.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.ItemModelRegistry;
import timmychips.pommelheldmodels.property.type.ItemModelRootDefinition;

/**
 * Lets the JSON boolean, "hand_animation_on_swap" disable the equipment animation when swapping items
 */
@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemSwapMixin {

    ///  Not accurate to vanilla, simply disables any swapping animation for item models (both for main hand and offhand)
    @Unique
    private float doModelHandSwap(float equipProgress) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer != null) {
            ItemStack heldItem = !clientPlayer.getMainHandStack().isEmpty() ? clientPlayer.getMainHandStack() : clientPlayer.getOffHandStack();
            Identifier heldId = Registries.ITEM.getId(heldItem.getItem());
            ItemModelRootDefinition def = ItemModelRegistry.getRoot(heldId); // Get items model definition for item model

            if (def != null && !def.handAnimationSwap()) return 0F; // Disable hand animation swap if current held item model has hand swap set to false
        }
        return equipProgress; // Normal hand animation swap
    }

    /// Accurate to vanilla, disables the main hand to offhand animation swap
    @Unique
    private float doModelOffHandSwap(float equipProgress) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer != null) {
            ItemStack offhandItem = clientPlayer.getOffHandStack();
            Identifier offhandId = Registries.ITEM.getId(offhandItem.getItem());
            ItemModelRootDefinition def = ItemModelRegistry.getRoot(offhandId); // Get items model definition for item model

            if (def != null && !def.handAnimationSwap()) return 0F; // Disable offhand animation swap if current held item model has hand swap set to false
        }
        return equipProgress; // Normal hand animation swap
    }

    @ModifyArg(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"
            ),
            index = 3 // equipProgress parameter index
    )
    private float disableEmptyHandEquipProgress(float equipProgress) {
        return doModelOffHandSwap(equipProgress);
    }

    @ModifyArg(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"
            ),
            index = 2 // equipProgress parameter index
    )
    private float disableItemEquipProgress(float equipProgress) {
        return doModelOffHandSwap(equipProgress);
    }
}
