package timmychips.pommelheldmodels.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timmychips.pommelheldmodels.GroundItemSubmerged;
import timmychips.pommelheldmodels.HeldItemPredicate;

@Environment(EnvType.CLIENT)
@Mixin(ItemEntityRenderer.class)
public class GroundItemMixin {

    // Affects item render predicate to item entities dropped onto the ground (i.e. not THROWN entities)
    @Inject(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"))
    public void renderGround(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        HeldItemPredicate.currentItemRenderMode = ModelTransformationMode.GROUND; // Set predicate to ground since it'll always be on the ground
        submergedInFluidCheck(itemEntity);
    }

    @Unique
    private static void submergedInFluidCheck(ItemEntity itemEntity) {
        HeldItemPredicate.groundItemEntitySubmerged = itemEntity.isSubmergedInWater();
        if (itemEntity.isSubmergedInWater()) {
            new GroundItemSubmerged().addItemEntity(itemEntity);
        }
//        Vec3d eyePos = itemEntity.getEyePos();
//        BlockPos fluidBlock = BlockPos.ofFloored(eyePos);
//        FluidState fluidState = itemEntity.getWorld().getFluidState(fluidBlock);
//        return !fluidState.isEmpty() ? 1.0F : 0.0F;
    }
}
