package timmychips.pommelheldmodels.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.objects.ProjectileModelMap;

@Mixin(Entity.class)
public abstract class ProjectileEntityOnRemovedMixin {
    @Inject(method = "onRemoved", at = @At(value = "HEAD"))
    private void onProjectileRemoved(CallbackInfo ci) {
        Entity self = (Entity)(Object)this;

        // Removes target projectile or flying object from projectile map when being removed
        if (self instanceof PersistentProjectileEntity || self instanceof FlyingItemEntity) {
            ProjectileModelMap.removeEntity(self);
        }
    }
}
