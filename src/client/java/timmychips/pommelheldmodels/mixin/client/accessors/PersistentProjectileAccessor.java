package timmychips.pommelheldmodels.mixin.client.accessors;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Invokes protected asItemStack() method to get the PersistentProjectileEntity's stored ItemStack
@Mixin(PersistentProjectileEntity.class)
public interface PersistentProjectileAccessor {
    @Invoker("asItemStack")
    ItemStack pommel$invokeAsItemStack();
}
