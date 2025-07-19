package timmychips.pommelheldmodels.resolver.rangeentry;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.resolver.RangePropertyHandler;
import timmychips.pommelheldmodels.type.RangeDispatchDefinition;

public class CompassFloat implements RangePropertyHandler {

    public static final Logger LOGGER = LogUtils.getLogger();

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

    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition def) {
        if (entity == null) return 0f;
        if (!(entity.getWorld() instanceof ClientWorld clientWorld)) return 0f;

        if (def.target() == null) return 0f;
        GlobalPos pos = getTargetPosition(clientWorld, stack, entity, def.target());

        if (!canPointTo(entity, pos)) return (float) Math.random(); // Random aimless direction

        float angle = getAngleTo(entity, pos.pos());
        float yaw = getBodyYaw(entity);
        float adjusted = 0.5f - (yaw - 0.25f - angle);
        return MathHelper.floorMod(adjusted, 1.0f);
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

    private boolean canPointTo(LivingEntity entity, GlobalPos pos) {
        return pos != null
                && pos.dimension() == entity.getWorld().getRegistryKey()
                && pos.pos().getSquaredDistance(entity.getPos()) >= 1e-5;
    }

    private float getAngleTo(LivingEntity entity, BlockPos pos) {
        Vec3d target = Vec3d.ofCenter(pos);
        return (float) (Math.atan2(target.z - entity.getZ(), target.x - entity.getX()) / (2 * Math.PI));
    }

    private float getBodyYaw(LivingEntity entity) {
        return MathHelper.floorMod(entity.getBodyYaw() / 360.0F, 1.0F);
    }
}
