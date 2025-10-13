package timmychips.pommelheldmodels.property.type;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import timmychips.pommelheldmodels.ClientInitializer;
import timmychips.pommelheldmodels.property.resolver.ResolveRecursive;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record CompositeItemModel(List<ItemModelDefinition> modelParts, ModelTransformationMode renderMode, ItemStack stack, LivingEntity entity) implements BakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        // You’ll need renderMode + stack + entity passed somehow; maybe wrap ResolveRecursive here
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .flatMap(baked -> baked.getQuads(state, face, random).stream())
                .toList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::useAmbientOcclusion);
    }

    @Override
    public boolean hasDepth() {
        return modelParts.stream().map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::hasDepth);
    }

    @Override
    public boolean isSideLit() {
        return modelParts.stream().map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::isSideLit);
    }

    @Override
    public boolean isBuiltin() {
        return modelParts.stream().map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .anyMatch(BakedModel::isBuiltin);
    }

    @Override
    public Sprite getParticleSprite() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .findFirst() // pick the first resolved model
                .map(BakedModel::getParticleSprite)
                .orElse(MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite());
    }

    static Transformation extractTransformation(BakedModel model, ModelTransformationMode mode) {
        if (model != null) {
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

    static ModelTransformation copyTransformations(BakedModel part) {
        Transformation transformation = extractTransformation(part, ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = extractTransformation(part, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = extractTransformation(part, ModelTransformationMode.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = extractTransformation(part, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = extractTransformation(part, ModelTransformationMode.HEAD);
        Transformation transformation6 = extractTransformation(part, ModelTransformationMode.GUI);
        Transformation transformation7 = extractTransformation(part, ModelTransformationMode.GROUND);
        Transformation transformation8 = extractTransformation(part, ModelTransformationMode.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    // TODO only gets transformations from the first model, need to do per-part transformations
    @Override
    public ModelTransformation getTransformation() {
        List<BakedModel> children = modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .toList();

        for (BakedModel part : children) {
            return copyTransformations(part);
        }
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .findFirst()
                .map(BakedModel::getOverrides)
                .orElse(ModelOverrideList.EMPTY);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false; // False to trigger FabricBakedModel rendering
    }

    private static Quaternionf eulerToQuaternion(Vector3f rotation) {
        float yaw = rotation.x; float pitch = rotation.y; float roll = rotation.z;
        double qx = Math.sin(roll/2) * Math.cos(pitch/2) - Math.cos(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2);
        double qy = Math.cos(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2);
        double qz = Math.cos(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2) - Math.sin(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2);
        double qw = Math.cos(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2);
        return new Quaternionf(qx, qy, qz, qw);
    }

    /*
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
//        ms.translate(-0.5F, -0.5F, -0.5F);

        float newX = (float) Math.toRadians(rotation.x % 360);
        float newY = (float) Math.toRadians(rotation.y % 360);
        float newZ = (float) Math.toRadians(rotation.z % 360);

        Quaternionf rotationQuaternion = new Quaternionf();
        rotationQuaternion.rotationXYZ(newX, newY, newZ);

        Quaternionf conjugate = rotationQuaternion.conjugate();

//        ms.multiply(rotationQuaternion.mul(conjugate));

        // Scale
        ms.scale(scale.x, scale.y, scale.z);

        // Extract the current model (position) matrix
        Matrix4f matrix = new Matrix4f(ms.peek().getPositionMatrix()); // method name depends on mapping: getModel() / getPositionMatrix()
        matrix = new Matrix4f(ms.peek().getPositionMatrix());
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

     */

    private static Matrix4f toMatrix(Transformation t) {
        Matrix4f matrix = new Matrix4f();
//        matrix.identity();

        // Translation
        Vector3f translation = t.translation;
//        translation = new Vector3f(1.13f,3.2f,1.13f);

        matrix.translate(translation.x(), translation.y(), translation.z());
//        matrix.translate(-0.0F, -0.5F, -0.0F);


        Vector3f rotation = t.rotation;
//        rotation = new Vector3f(0, -90, 25);
//        float newX = (float) Math.toRadians(rotation.x % 360);
        float newX = (float) Math.toRadians(rotation.x);
        float newY = (float) Math.toRadians(rotation.y);
        float newZ = (float) Math.toRadians(rotation.z);

        // Rotation
//        Quaternionf rotation = t.rotation;
//        matrix.rotate(rotation);

        Quaternionf rotationQuaternion = new Quaternionf();
        Quaternionf newQuaternion = eulerToQuaternion(new Vector3f(newX, newY, newZ));
        Quaternionf newConjugate = newQuaternion.conjugate();

        rotationQuaternion.rotationXYZ(newX, newY, newZ);

        Quaternionf conjugate = rotationQuaternion.conjugate();

//        matrix.rotate(rotationQuaternion.mul(conjugate));
        matrix.rotate(newQuaternion);

        // Scale
        Vector3f scale = t.scale;
//        scale = new Vector3f(0.68f, 0.68f, 0.68f);
        matrix.scale(scale.x(), scale.y(), scale.z());

        // Right rotation (used for mirroring / composite transforms)
//        Quaternionf rightRot = t.getRightRotation();
//        matrix.rotate(rightRot);

        return matrix;
    }

    private static Matrix4f mergeTransformations(List<Transformation> transforms) {
        Matrix4f result = new Matrix4f();
        result.identity();

        for (Transformation t : transforms) {
            Matrix4f childMatrix = toMatrix(t);
            result.mul(childMatrix); // multiply in order
        }

        return result;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        List<BakedModel> bakedModels = modelParts.stream()
                .map(part -> ResolveRecursive.resolve(part, renderMode, stack, entity)
                        .orElse(ResolveRecursive.getMissingModel()))
                .toList();

        List<Transformation> transforms = new ArrayList<>();
        for (BakedModel part : bakedModels) {
            ModelTransformation childTx = part.getTransformation();
            Transformation t = childTx.getTransformation(renderMode);
            transforms.add(t);
        }

        // Merge all transformations into a single matrix
        Matrix4f merged = mergeTransformations(transforms);

        // Apply it once to the context
//        context.pushTransform(quad -> {
//            for (int i = 0; i < 4; i++) {
//                Vector4f pos = new Vector4f(quad.x(i), quad.y(i), quad.z(i), 1.0f);
//                pos.mul(merged);
//                quad.pos(i, pos.x(), pos.y(), pos.z());
//            }
//            return true;
//        });
        MatrixStack matrixStack = new MatrixStack();
        MatrixStack.Entry entry = matrixStack.peek().copy();

        // test pushTransform

        // Emit all quads
        for (BakedModel part : bakedModels) {
            part.emitItemQuads(stack, randomSupplier, context);
        }

        // Needs to be here if you push the transform
//        context.popTransform();
    }
}
