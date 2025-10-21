package timmychips.pommelheldmodels.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.mixin.client.accessors.PersistentProjectileAccessor;
import timmychips.pommelheldmodels.objects.ProjectileModelMap;

@Environment(EnvType.CLIENT)
@Mixin(value = EntityRenderer.class, priority = 1)
public abstract class ProjectileEntityMixin<T extends Entity> {

    /**
     * Set projectile value to true when it renders a projectile entity for model predicate
     */
    @Inject(method = "render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    public void setProjectileField(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        ///  For PersistentProjectiles (arrows) from invoker field
        if (entity instanceof PersistentProjectileEntity persistent) {
            ItemStack stack = ((PersistentProjectileAccessor) persistent).pommel$invokeAsItemStack();
            addToMap(entity, stack);

            if (entity.isRemoved()) ProjectileModelMap.removeEntity(entity); // Removes from map here since onRemove() method doesn't properly remove it
        }

        /// FlyingItems (egg, snowball, eye of ender) from interface method
        if (entity instanceof FlyingItemEntity flyingEntity) {
            ItemStack stack = flyingEntity.getStack();
            addToMap(entity, stack);
        }
    }

    // Add ItemStack to projectile map when not marked for deletion
    @Unique
    private static void addToMap(Entity entity, ItemStack stack) {
        if (!entity.isRemoved() && !ProjectileModelMap.entityInMap(entity)) {
            ProjectileModelMap.addEntity(entity, stack);
        }
    }
}