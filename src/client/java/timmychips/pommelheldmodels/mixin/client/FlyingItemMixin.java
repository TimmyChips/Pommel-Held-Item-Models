package timmychips.pommelheldmodels.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.HeldItemPredicate;
import timmychips.pommelheldmodels.objects.ProjectileModelMap;

/**
 * Mainly for Ender Eye entities, which don't extend ProjectileEntity class
 */
@Environment(EnvType.CLIENT)
@Mixin(value = FlyingItemEntityRenderer.class, priority = 1)
public class FlyingItemMixin<T extends Entity & FlyingItemEntity> {

    @Inject(method = "render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"))
    public void renderFlying(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
//        HeldItemPredicate.isProjectile = true;
//        ItemStack stack = entity.getStack();
//        if (!entity.isRemoved() && !ProjectileModelMap.entityInMap(entity)) {
//            ProjectileModelMap.addEntity(entity, stack);
//        }
    }
}
