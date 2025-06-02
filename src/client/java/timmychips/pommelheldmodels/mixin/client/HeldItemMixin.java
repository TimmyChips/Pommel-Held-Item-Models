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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.HeldItemPredicate;
import timmychips.pommelheldmodels.UseKeyTracker;

// Mixin injects into target ItemRenderer vanilla class
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    // Sets item render predicate to 0.0 or 1.0 based on the current render mode
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At(value = "HEAD"))
    private void pommel$renderHeldItem(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (entity != null) HeldItemPredicate.itemInOffhand = entity.getOffHandStack() == item; // True if current item in entity's offhand
//        HeldItemPredicate.itemBeingUsed = UseKeyTracker.getItemUsed() == item;
//        System.out.println(HeldItemPredicate.itemBeingUsed);

//        if (entity != null) LOGGER.info(String.valueOf(entity.getId()));
//        if (ClientInitializer.player_ent != null && entity != null) {
//            if (ClientInitializer.player_ent.getId() == entity.getId()) {
//                LOGGER.info("matched entity");
//                ClientInitializer.player_ent = null;
//                HeldItemPredicate.isUsingItemFloat = 1.0F;
//            }
//        }

//        LOGGER.info(String.valueOf(item.getItem()));
//        if (entity != null) HeldItemPredicate.activeItem = item.getItem();
//        UseKeyTracker.itemUsingLerp();

        UseKeyTracker.player_usedItemTimer();

        HeldItemPredicate.currentItemRenderMode = renderMode; // Sets the item model's "is_held" item predicate based on renderMode
    }

    // Resets the item back to the base model when it's in the GUI, on the Ground, or in an Item Frame
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"))
    private void pommel$renderBaseItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        HeldItemPredicate.currentItemRenderMode = null; // Resets the item predicate so it renders the 2d model
    }
}
