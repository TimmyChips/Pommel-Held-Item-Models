package timmychips.pommelheldmodels.mixin.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.HeldItemPredicate;
import timmychips.pommelheldmodels.ItemModelResolver;
import timmychips.pommelheldmodels.UseKeyTracker;

import java.util.Optional;
import java.util.Set;

// Mixin injects into target ItemRenderer vanilla class
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    @Shadow @Final private ItemModels models;
    private static final Logger LOGGER = LogUtils.getLogger();

    @Unique
    private static final ThreadLocal<LivingEntity> CURRENT_ENTITY = new ThreadLocal<>();

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

            CURRENT_ENTITY.set(entity);
            if (entity != null) {
                LOGGER.info(String.valueOf(entity.getActiveItem() != item ? 0.0F : (float)(item.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F));
            }

//          UseKeyTracker.itemUsingLerp();
            UseKeyTracker.tickTimer(entity); // Countdown tick timer for other (non-client) players to retain item usage
        }

//        HeldItemPredicate.currentItemRenderMode = renderMode; // Sets the item model's "is_held" and other item predicates based on renderMode
    }

    // Resets the item back to the base model when it's in the GUI
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"), cancellable = true)
    private void pommel$renderBaseItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel originalModel, CallbackInfo ci) {
//        HeldItemPredicate.currentItemRenderMode = null; // Resets the item predicate so it renders the 2d model

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        LivingEntity entity = CURRENT_ENTITY.get();

        Optional<Identifier> maybeModelId = ItemModelResolver.resolveModel(itemId, renderMode, stack, entity);

        maybeModelId.ifPresent(modelId -> {

            String cleanPath = modelId.getPath().startsWith("item/")
                    ? modelId.getPath().substring("item/".length())
                    : modelId.getPath();

            ModelIdentifier modelIdentifier = new ModelIdentifier(Identifier.of("minecraft", cleanPath), "inventory"); // gets correct path
            BakedModel customModel = this.models.getModelManager().getModel(modelIdentifier);

            if (customModel != null && customModel != originalModel) {

                ItemRenderer self = (ItemRenderer)(Object)this;
                self.renderItem(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, customModel);

                ci.cancel();
            }
        });
    }
}
