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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.HeldItemPredicate;

import static timmychips.pommelheldmodels.UseKeyTracker.itemMap;

// Mixin injects into target ItemRenderer vanilla class
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    // Sets item render predicate to 0.0 or 1.0 based on the current render mode or other conditions
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At(value = "HEAD"))
    private void pommel$renderHeldItem(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {

        if (entity != null) HeldItemPredicate.itemInOffhand = entity.getOffHandStack() == item; // True if current item in entity's offhand

        validateMatchingUsedItem(entity);

        // Replaces the render mode for these entities from using the GROUND render mode to using a third person render mode for rendering held item models
        if (entity instanceof VillagerEntity || entity instanceof WitchEntity || entity instanceof PandaEntity) {
            renderMode = ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
        }

        HeldItemPredicate.currentItemRenderMode = renderMode; // Sets the item model's "is_held" and other item predicates based on renderMode
    }

    // Resets the item back to the base model when it's in the GUI
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"))
    private void pommel$renderBaseItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        HeldItemPredicate.currentItemRenderMode = null; // Resets the item predicate so it renders the 2d model
        HeldItemPredicate.isFlyingItem = false; // Resets flying item field
    }

    // Validates if the player's currently held item matches item used map
    // If player's current ItemStack doesn't match the ItemStack in that player's key in the map, it removes that player key from map
    @Unique
    private void validateMatchingUsedItem(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity player) {
//            Hand hand = player.getActiveHand();
//            ItemStack currentStack = player.getStackInHand(hand); // Only does it for player's main hand :(

            // copies code from UseKeyTracker
            ItemStack currentStack = livingEntity.getMainHandStack().isEmpty() ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
            ItemStack defaultedStack = currentStack.getItem().getDefaultStack();

            if (itemMap.containsKey(player) && !ItemStack.areEqual(defaultedStack, itemMap.get(player).lastItem)) {
                itemMap.remove(player);
            }
        }
    }
}
