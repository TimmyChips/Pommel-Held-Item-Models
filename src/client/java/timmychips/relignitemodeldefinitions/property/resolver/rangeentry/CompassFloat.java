package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

public class CompassFloat implements RangePropertyHandler {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static float compassAngle;
    private final AngleInterpolator aimedInterpolator = new AngleInterpolator();
    private final AngleInterpolator aimlessInterpolator = new AngleInterpolator();

    public enum CompassTarget implements StringIdentifiable {
        NONE("none"),
        LODESTONE("lodestone"),
        SPAWN("spawn"),
        RECOVERY("recovery");

        private final String name;

        CompassTarget(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }

        public static final Codec<CompassTarget> CODEC = StringIdentifiable.createCodec(CompassTarget::values);
    }

    // Get the angle of the compass and return as float
    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        if (entity == null) return 0f;
        if (!(entity.getWorld() instanceof ClientWorld clientWorld)) return 0f;

        if (def.target() == null) return 0f; // target is not defined or is none

        GlobalPos pos = getTargetPosition(clientWorld, stack, entity, def.target());
        long time = clientWorld.getTime();
        boolean should_wobble = Boolean.TRUE.equals(def.wobble());

        if (!canPointTo(entity, pos)) return this.getAimlessAngle(0, time, should_wobble); // compass randomly rotates if it cant point at block

        // Angle float calculation
        double angle = this.getAngleTo(entity, pos.pos());
        double yaw = this.getBodyYaw(entity);
        double adjusted;

        // Performs interpolated wobble if true
        if (should_wobble) {
            if (this.aimedInterpolator.shouldUpdate(time)) {
                this.aimedInterpolator.update(time, 0.5 - (yaw - 0.25));
            }

            adjusted = angle + this.aimedInterpolator.value;
        }
        else adjusted = 0.5 - (yaw - 0.25 - angle); // immediately points in direction; no interpolation

        return MathHelper.floorMod((float) adjusted, 1.0F);
    }

    private float getAimlessAngle(int seed, long time, boolean should_wobble) {
        // Interpolated random rotation
        if (should_wobble) {
            if (this.aimlessInterpolator.shouldUpdate(time)) {
                this.aimlessInterpolator.update(time, Math.random());
            }

            double d = this.aimlessInterpolator.value + (double) ((float) this.scatter(seed) / 2.14748365E9F);
            return MathHelper.floorMod((float)d, 1.0F);
        }
        // Non-interpolated random rotation
        return MathHelper.floorMod((float) this.scatter(seed) / 2.14748365E9F, 1.0F);
    }

    private GlobalPos getTargetPosition(ClientWorld world, ItemStack stack, LivingEntity holder, CompassTarget target) {
        return switch (target) {
            case LODESTONE -> {
                LodestoneTrackerComponent comp = stack.get(DataComponentTypes.LODESTONE_TRACKER);
                yield comp != null ? comp.target().orElse(null) : null;
            }
            case SPAWN -> GlobalPos.create(world.getRegistryKey(), world.getSpawnPos());
            case RECOVERY -> {
                if (holder instanceof PlayerEntity player)
                    yield player.getLastDeathPos().orElse(null);
                yield null;
            }
            case NONE -> null;
        };
    }

    private float getAngleTo(LivingEntity entity, BlockPos pos) {
        Vec3d target = Vec3d.ofCenter(pos);
        return (float) (Math.atan2(target.getZ() - entity.getZ(), target.getX() - entity.getX()) / (2 * Math.PI));
    }

    private boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
        return pos != null && pos.dimension() == entity.getWorld().getRegistryKey() && !(pos.pos().getSquaredDistance(entity.getPos()) < 9.999999747378752E-6);
    }

    private float getBodyYaw(LivingEntity entity) {
        return MathHelper.floorMod(entity.getBodyYaw() / 360.0F, 1.0F);
    }

    private double scatter(int seed) {
        return seed * 1327217883F;
    }

    @Environment(EnvType.CLIENT)
    static class AngleInterpolator {
        double value;
        private double speed;
        private long lastUpdateTime;

        AngleInterpolator() {
        }

        boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        void update(long time, double target) {
            this.lastUpdateTime = time;
            double d = target - this.value;
            d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
            this.speed += d * 0.1;
            this.speed *= 0.8;
            this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
        }
    }
}
