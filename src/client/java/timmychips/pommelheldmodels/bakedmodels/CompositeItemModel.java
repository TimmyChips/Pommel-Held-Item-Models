package timmychips.pommelheldmodels.bakedmodels;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import timmychips.pommelheldmodels.property.resolver.ResolveRecursive;
import timmychips.pommelheldmodels.property.type.ItemModelDefinition;

import java.util.List;
import java.util.function.Supplier;

public class CompositeItemModel implements BakedModel {
    private final List<WrappedBakedModel> parts;
    private ModelTransformationMode renderMode;

    public CompositeItemModel(List<WrappedBakedModel> parts, ModelTransformationMode renderMode) {
        this.parts = parts;
        this.renderMode = renderMode;
    }

    // Build a matrix from a Transformation using MatrixStack
    private static Matrix4f matrixFromTransformation(Transformation t) {
        // Create a MatrixStack and apply transformation in the same order that vanilla expects
        MatrixStack ms = new MatrixStack();
        ms.push();

        // NOTE: the arrays/fields names may differ on your Transformation class;
        // read translation/rotation/scale from the Transformation instance.
        Vector3f translation = t.translation; // pseudo - replace with your getter
        Vector3f rotation = t.rotation;       // pseudo - replace with your getter (degrees)
        Vector3f scale = t.scale;             // pseudo

        // Translation (x, y, z)
        ms.translate(translation.x, translation.y, translation.z);

        // Rotation: vanilla uses rotation vector as (x, y, z) degrees.
        // Convert degrees -> radians as needed and multiply quaternions / rotate with MatrixStack
//        if (rotation.x != 0f) ms.multiply(new Quaternionf(rotation.));
//        if (rotation.y != 0f) ms.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)Math.toRadians(rotation[1])));
//        if (rotation.z != 0f) ms.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion((float)Math.toRadians(rotation[2])));

        float newX = (float) Math.toRadians(rotation.x % 360);
        float newY = (float) Math.toRadians(rotation.y % 360);
        float newZ = (float) Math.toRadians(rotation.z % 360);

        Quaternionf rotationQuaternion = new Quaternionf();
        rotationQuaternion.rotationXYZ(newX, newY, newZ);

        Quaternionf conjugate = rotationQuaternion.conjugate();

        ms.multiply(rotationQuaternion.mul(rotationQuaternion));

        // Scale
        ms.scale(scale.x, scale.y, scale.z);

        // Extract the current model (position) matrix
        Matrix4f matrix = new Matrix4f(ms.peek().getPositionMatrix()); // method name depends on mapping: getModel() / getPositionMatrix()
        ms.pop();
        return matrix;
    }

    // Transform a single quad using a Matrix4f
    private static boolean transformQuadWithMatrix(MutableQuadView quad, Matrix4f mat) {
        // For each of the 4 vertices:
        for (int v = 0; v < 4; v++) {
            // read vertex coords - replace these with your MutableQuadView getters
            float x = quad.x(v); // PSEUDO: find actual getter (maybe pos, maybe getPos)
            float y = quad.y(v);
            float z = quad.z(v);

            // transform
            Vector4f p = new Vector4f(x, y, z, 1.0f);
            mat.transform(p); // if this method doesn't exist, try Matrix4f.transform(Vector4f) or mat.multiply(p)

            // write back - replace with actual setter method on MutableQuadView
//            quad.setVertexPos(v, p.x(), p.y(), p.z());
            quad.pos(v, p.x(), p.y(), p.z());
        }
        return true; // keep this quad (returning false would drop it)
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        for (WrappedBakedModel child : parts) {
//            ModelTransformation childTx = child.getPommelTransformations();
//            Transformation t = childTx.getTransformation(renderMode);
//            Matrix4f childMatrix = matrixFromTransformation(t);
//
//            context.pushTransform(quad -> transformQuadWithMatrix(quad, childMatrix));

            child.emitItemQuads(stack, randomSupplier, context);
            parts.getFirst().emitItemQuads(stack, randomSupplier, context);
            parts.getLast().emitItemQuads(stack, randomSupplier, context);

            context.popTransform();
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        for (WrappedBakedModel part : parts) {
            quads.addAll(part.getQuads(state, face, random));
        }
        return quads.build();
    }

    // Delegate other methods (AO, particles, overrides, etc.)
    @Override
    public boolean useAmbientOcclusion() {
        return parts.stream().anyMatch(WrappedBakedModel::useAmbientOcclusion);
    }

    @Override
    public Sprite getParticleSprite() {
        Sprite missingModelParticle = MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite();
        return parts.isEmpty() ? missingModelParticle : parts.getFirst().getParticleSprite();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY; // Probably can be empty since we're using a different system, not Item Model Overrides?
    }

    @Override
    public boolean hasDepth() {
        return parts.stream().anyMatch(WrappedBakedModel::hasDepth);
    }

    @Override
    public boolean isSideLit() {
        return parts.stream().allMatch(WrappedBakedModel::isSideLit);
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    static Transformation extractTransformation(List<WrappedBakedModel> parts, ModelTransformationMode mode) {
        for(WrappedBakedModel model : parts) {
            ModelTransformation modelTransformation = model.getTransformation();
            if (modelTransformation != null) {
                Transformation transformation = modelTransformation.getTransformation(mode);
                if (transformation != Transformation.IDENTITY) {
                    return transformation;
                }
            }
        }

        return Transformation.IDENTITY;
    }

    static ModelTransformation copyTransformations(List<WrappedBakedModel> parts) {
        Transformation transformation = extractTransformation(parts, ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = extractTransformation(parts, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = extractTransformation(parts, ModelTransformationMode.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = extractTransformation(parts, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = extractTransformation(parts, ModelTransformationMode.HEAD);
        Transformation transformation6 = extractTransformation(parts, ModelTransformationMode.GUI);
        Transformation transformation7 = extractTransformation(parts, ModelTransformationMode.GROUND);
        Transformation transformation8 = extractTransformation(parts, ModelTransformationMode.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    @Override
    public ModelTransformation getTransformation() {
        // You *could* merge here, but better: always defer per-child in emitItemQuads
//        for (WrappedBakedModel child : parts) {
//            return copyTransformations(child);
//        }
//        return ModelTransformation.NONE;
//        return copyTransformations(parts)

        if (parts.isEmpty()) {
            return ModelTransformation.NONE;
        }

        return parts.getLast().getTransformation();
    }
}
