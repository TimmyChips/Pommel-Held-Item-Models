package timmychips.pommelheldmodels.mixin.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.ItemModelRegistry;
import timmychips.pommelheldmodels.property.type.CompositeItemModel;

import java.util.List;
import java.util.Optional;

import static timmychips.pommelheldmodels.property.resolver.ItemModelResolver.resolveModel;

// Mixin injects into target ItemRenderer vanilla class
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO
    //  Add a second items folder maybe called "mymod_items_override" to allow modded properties while having the resource pack still work seamlessly w/ vanilla
    //  Refactor other properties and types to follow what was done with the compass instead of using switches

    // TODO
    //  Make if no fallback field is specified for minecraft:select or minecraft:range_dispatch types in items.json, make it return a missing model
    //  If there's any error with the model, also show a missing model

    // Gets custom model for GUI model mode so the item model changes for the GUI
    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true)
    private void pommel$overrideGUIModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel gui_model = getCustomModel(stack, entity, ModelTransformationMode.GUI);
        if (gui_model != null) {
            cir.setReturnValue(gui_model);
        }
    }

    @Shadow
    private void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {/*dummy body*/}

    @Shadow
    private final BuiltinModelItemRenderer builtinModelItemRenderer = this.builtinModelItemRenderer;

    @Shadow
    private static boolean usesDynamicDisplay(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }

    @Shadow
    public static VertexConsumer getDirectItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint) {
        return glint
                ? VertexConsumers.union(provider.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getDirectEntityGlint()), provider.getBuffer(layer))
                : provider.getBuffer(layer);
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void renderCompositeModel(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            if (model instanceof CompositeItemModel compositeModel) {

                List<BakedModel> models = compositeModel.getModels();
                if (models != null) {
                    for (BakedModel modelPart : models) {
                        ClientInitializer.LOGGER.info(modelPart.toString());

                        matrices.push();

                        boolean bl = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
                        modelPart.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
                        matrices.translate(-0.5F, -0.5F, -0.5F);

                        if (!model.isBuiltin() || bl) {
                            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, true);
                            VertexConsumer vertexConsumer;

                            vertexConsumer = getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());

                            MatrixStack.Entry entry = matrices.peek().copy();
                            if (renderMode == ModelTransformationMode.GUI) {
                                MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
                            } else if (renderMode.isFirstPerson()) {
                                MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
                            }

                            this.renderBakedItemModel(modelPart, stack, light, overlay, matrices, vertexConsumer);
                        }
                        else {
                            this.builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
                        }
                        matrices.pop();
                    }
                }
                ci.cancel();
            }
        }
    }

    // Replaces entity item render with our custom model
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void pommel$interceptRender(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded,
                                        MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world,
                                        int light, int overlay, int seed, CallbackInfo ci) {

        BakedModel model = getCustomModel(item, entity, renderMode);
        if (model instanceof CompositeItemModel compositeItemModel) {
            ClientInitializer.LOGGER.info("Composite model: {}", compositeItemModel);
        }

        if (model != null) {
            ItemRenderer self = (ItemRenderer)(Object)this;
            // manually call vanilla rendering method with overridden model
            self.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
            ci.cancel(); // skip original call
        }
    }

    // Get custom model from BakedModelManger's getModel from id (which is needed since we loaded the models with ModelLoadingPlugin)
    @Unique
    private static BakedModel getCustomModel(ItemStack stack, LivingEntity entity, ModelTransformationMode mode) {

        BakedModelManager missingModelManager = MinecraftClient.getInstance().getBakedModelManager();

        // If item's items model definition has an invalid model type, returns missing item model
        for (Identifier id : ItemModelRegistry.INVALID_MODEL_TYPES) {
            if (Registries.ITEM.getId(stack.getItem()).equals(id)) { // Checks if INVALID_TYPES Set contains item id
                return missingModelManager.getMissingModel(); // Item renders as Missing Model
            }
        }

        if (mode == null) mode = ModelTransformationMode.GUI;

//        Optional<Identifier> maybeModel = resolveModel(Registries.ITEM.getId(stack.getItem()), mode, stack, entity); // Get resolved model specified in items.json for the item
//        if (maybeModel.isPresent()) {
//            Identifier modelId = maybeModel.get();
//
//            if (modelId.toString().equals("pommel:missingno")) { // No Fallback Model specified
//                return missingModelManager.getMissingModel(); // Item renders as Missing Model
//            }
//
//            FabricBakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
//            return manager.getModel(modelId); // Use Identifier; Can't use ModelIdentifier since our loaded models don't have corresponding ModelIdentifiers
//        }

        Optional<BakedModel> maybeModel = resolveModel(Registries.ITEM.getId(stack.getItem()), mode, stack, entity);
        if (maybeModel != null && maybeModel.isPresent()) {
            BakedModel bakedModel = maybeModel.get();

            return bakedModel;
        }
        return null;
    }
}

