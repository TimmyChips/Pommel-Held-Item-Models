package timmychips.pommelheldmodels.mixin.client;

import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import timmychips.pommelheldmodels.HeldModelManager;

// Mixin injects into target ItemRenderer vanilla class
@Mixin(ItemRenderer.class)
public abstract class HeldItemMixin {

    @Final
    @Shadow
    private ItemModels models;

    // Leftover for me to not forgor
    // Example identifier to specific item
    @Unique
    private static Identifier EXAMPLE = Identifier.of("minecraft", "held_models/diamond_pickaxe");

    @Shadow
    protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);

    @Shadow
    @Final
    private BuiltinModelItemRenderer builtinModelItemRenderer;

    // Adds this code to top of the first renderItem Operator in ItemRenderer target class
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {

        // Ensures renderMode is either inventory, dropped item, or item frame
        boolean notHeld = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
        if (!stack.isEmpty() && HeldModelManager.hasHeldModel(stack.getItem().toString()) && notHeld) {

            // Must push a copy of the matrices for proper rendering (from vanilla ItemRenderer class)
            matrices.push();

            model = this.models.getModel(stack.getItem()); // Returns default inventory item model, aka not our custom held model

            // All vanilla code taken from ItemRenderer class and slightly tweaked
            model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
            matrices.translate(-0.5F, -0.5F, -0.5F);

            if (!model.isBuiltin() && (!stack.isOf(Items.TRIDENT))) {
                boolean notBlock;
                if (renderMode != ModelTransformationMode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    notBlock = !(block instanceof TranslucentBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    notBlock = true;
                }

                RenderLayer renderLayer = RenderLayers.getItemLayer(stack, notBlock);
                VertexConsumer vertexConsumer;
                if ((stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK)) && stack.hasGlint()) {
                    MatrixStack.Entry entry = matrices.peek().copy();
                    if (renderMode == ModelTransformationMode.GUI) {
                        MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
                    } else if (renderMode.isFirstPerson()) {
                        MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
                    }

                    vertexConsumer = ItemRenderer.getDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
                } else if (notBlock) {
                    vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
                } else {
                    vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
                }

                this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
            } else {
                this.builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
            }

            matrices.pop();
            ci.cancel(); // Operator is finished
        }
    }

    // Define your custom ModelIdentifiers
    //private static final ModelIdentifier CUSTOM_STICK_HELD_MODEL = ModelIdentifier.ofInventoryVariant(Identifier.of("minecraft:stick"));
    @Inject(method = "getModel", at = @At(value = "HEAD"), cancellable = true)
    private void getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel; // model that will be for the held item
        Item item = stack.getItem(); // Gets the item

        // Replaces the held item model if the item is a Key in HashMap
        if (HeldModelManager.hasHeldModel(item.toString())) {
            Identifier heldModel = HeldModelManager.getHeldModel(item.toString()); // Gets the identifier Value (aka the held model path)

            if (this.models.getModelManager().getModel(heldModel) != null) { // Ensures model is actually registered
                bakedModel = this.models.getModelManager().getModel(heldModel); // Held Item model is set to custom model
            } else {
                bakedModel = this.models.getModel(item); // Returns regular vanilla item model if model isn't registered
                                                         // When resource packs are reloaded (F3 + T), the mixin tries to fetch the model before the model is actually registered
            }

            cir.setReturnValue(bakedModel); // Returns baked model for rendering
        }
    }
}
