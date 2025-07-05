package timmychips.pommelheldmodels.resolver.condition;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FishingRodCastBool {
    public static Boolean test(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            return player.fishHook != null;
        }
        else return false;
    }
}
