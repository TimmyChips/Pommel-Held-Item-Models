package timmychips.relignitemodeldefinitions.property.resolver.rangeentry;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import timmychips.relignitemodeldefinitions.property.handler.RangePropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.RangeDispatchDefinition;

public class ClockTimeFloat implements RangePropertyHandler {
    private final Random random = Random.create();
    private final ClockAngler.Angler wobbleAngler;
    private final ClockAngler.Angler instantAngler;

    // Constructor for anglers
    public ClockTimeFloat() {
        this.wobbleAngler = new ClockAngler().createAngler(0.9F, true);
        this.instantAngler = new ClockAngler().createAngler(0.9F, false);
    }

    @Override
    public float getValue(ItemStack stack, LivingEntity entity, RangeDispatchDefinition.Definition definition) {
        if (entity == null) return 0F;
        if (!(entity.getWorld() instanceof ClientWorld clientWorld)) return 0f;

        // Specified source
        ClockSource source = definition.clockSource();

        boolean wobble = Boolean.TRUE.equals(definition.wobble());

        // Set angler to wobbled version or not
        ClockAngler.Angler angler;
        if (wobble) angler = wobbleAngler;
        else angler = instantAngler;

        if (source == null) return 0F;

        float targetAngle = source.getRandomAngle(clientWorld, stack, entity, this.random);
        long time = clientWorld.getTime();

        if (angler.shouldUpdate(time)) {
            angler.update(time, targetAngle);
        }
        return angler.getAngle();
    }

    public enum ClockSource implements StringIdentifiable {
        DAYTIME("daytime") {
            public float getRandomAngle(ClientWorld clientWorld, ItemStack stack, Entity user, Random random) {
                return clientWorld.getSkyAngle(1.0F);
            }
        },
        MOON_PHASE("moon_phase") {
            public float getRandomAngle(ClientWorld clientWorld, ItemStack stack, Entity user, Random random) {
                return (float) clientWorld.getMoonPhase() / 8F;
            }
        },
        RANDOM("random") {
            public float getRandomAngle(ClientWorld clientWorld, ItemStack stack, Entity user, Random random) {

                return random.nextFloat();
            }
        };

        public static final Codec<ClockSource> CODEC = StringIdentifiable.createCodec(ClockSource::values);
        private final String name;

        ClockSource(String name) { this.name = name; }
        public String asString() { return name; }
        abstract float getRandomAngle(ClientWorld world, ItemStack stack, Entity user, Random random);
    }
}


@Environment(EnvType.CLIENT)
class ClockAngler {

    public interface Angler {
        float getAngle();
        boolean shouldUpdate(long time);
        void update(long time, float target);
    }

    protected Angler createAngler(float speedMultiplier, boolean should_wobble) {
        return should_wobble ? createWobblyAngler(speedMultiplier) : createInstantAngler();
    }

    public static Angler createWobblyAngler(final float speedMultiplier) {
        return new Angler() {
            private float angle;
            private float speed;
            private long lastUpdateTime;

            public float getAngle() {
                return this.angle;
            }

            public boolean shouldUpdate(long time) {
                return this.lastUpdateTime != time;
            }

            public void update(long time, float target) {
                this.lastUpdateTime = time;
                float f = MathHelper.floorMod(target - this.angle + 0.5F, 1.0F) - 0.5F;
                this.speed += f * 0.1F;
                this.speed *= speedMultiplier;
                this.angle = MathHelper.floorMod(this.angle + this.speed, 1.0F);
            }
        };
    }

    public static Angler createInstantAngler() {
        return new Angler() {
            private float angle;

            public float getAngle() {
                return this.angle;
            }

            public boolean shouldUpdate(long time) {
                return true;
            }

            public void update(long time, float target) {
                this.angle = target;
            }
        };
    }
}
