package timmychips.pommelheldmodels.mixin.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.HeldItemPredicate;
import timmychips.pommelheldmodels.UseKeyTracker;
import timmychips.pommelheldmodels.resolver.ItemModelResolver;
import timmychips.pommelheldmodels.resolver.SelectValueResolver;

import java.util.Optional;

import static timmychips.pommelheldmodels.resolver.ItemModelResolver.resolveModel;

// Mixin injects into target ItemRenderer vanilla class
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Unique
    private static final ThreadLocal<ModelTransformationMode> CURRENT_MODEL_MODE = new ThreadLocal<>();

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "HEAD"))
    private void pommel$setModelModeFromEntity(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        CURRENT_MODEL_MODE.set(renderMode);
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"))
    private void pommel$setModelModeFromStack(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel originalModel, CallbackInfo ci) {
        CURRENT_MODEL_MODE.set(renderMode);
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "HEAD"))
    private void pommel$clearModelMode(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        CURRENT_MODEL_MODE.remove();
    }



    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true)
    private void pommel_overrideModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel custom = HeldItemMixin.getCustomModel(stack, entity, world, seed);
        if (custom != null) {
            cir.setReturnValue(custom);
        }
    }

    @Unique
    private static BakedModel getCustomModel(ItemStack stack, LivingEntity entity, World world, int seed) {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());

        ModelTransformationMode mode = CURRENT_MODEL_MODE.get(); // or pass actual mode
        if (mode == null) mode = ModelTransformationMode.GUI;
        LOGGER.info(String.valueOf(mode));

        Optional<Identifier> maybeModel = resolveModel(itemId, mode, stack, entity);
        if (maybeModel.isPresent()) {
            Identifier modelId = maybeModel.get();

            // Construct proper ModelIdentifier with the "inventory" variant:
            ModelIdentifier variant = new ModelIdentifier(modelId, "inventory");

            FabricBakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
            BakedModel model = manager.getModel(modelId);

            return model;
        }
        return null;
    }
}

