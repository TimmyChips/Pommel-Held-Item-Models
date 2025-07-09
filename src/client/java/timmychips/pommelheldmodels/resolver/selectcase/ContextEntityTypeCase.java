package timmychips.pommelheldmodels.resolver.selectcase;

import net.minecraft.entity.LivingEntity;

public class ContextEntityTypeCase {
    public static String test(LivingEntity entity) {
        return entity != null ? entity.getType().getRegistryEntry().registryKey().getValue().toString() : null;
    }
}
