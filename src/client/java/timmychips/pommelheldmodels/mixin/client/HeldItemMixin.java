package timmychips.pommelheldmodels.mixin.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.HeldItemPredicate;
import timmychips.pommelheldmodels.UseKeyTracker;

// Mixin injects into target ItemRenderer vanilla class
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    // Sets item render predicate to 0.0 or 1.0 based on the current render mode or other conditions
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At(value = "HEAD"))
    private void pommel$renderHeldItem(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (entity != null) {
            HeldItemPredicate.itemInOffhand = entity.getOffHandStack() == item; // True if current item in entity's offhand
            HeldItemPredicate.isSubmerged = entity.isSubmergedInWater();
//            if (entity.isFallFlying()) LOGGER.info("is elyta flying");
//            if (!entity.isOnGround() && entity.fallDistance > 0.0) LOGGER.info("is falling");
//            LOGGER.info(String.valueOf(entity.fallDistance));
            HeldItemPredicate.isFallingCheck(entity);

//          UseKeyTracker.itemUsingLerp();
            UseKeyTracker.playerUsedItemTickTimer(entity); // Countdown tick timer for other (non-client) players to retain item usage
        }

        if (entity instanceof VillagerEntity || entity instanceof WitchEntity || entity instanceof PandaEntity) {
            renderMode = ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
        }

        HeldItemPredicate.currentItemRenderMode = renderMode; // Sets the item model's "is_held" and other item predicates based on renderMode
    }

    // Resets the item back to the base model when it's in the GUI
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"))
    private void pommel$renderBaseItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        HeldItemPredicate.currentItemRenderMode = null; // Resets the item predicate so it renders the 2d model
    }
}
