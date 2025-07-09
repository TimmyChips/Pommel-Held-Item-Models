package timmychips.pommelheldmodels.resolver.selectcase;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

public class MainHandCase {
    public static String test(LivingEntity entity) {
        Arm mainArm = MinecraftClient.getInstance().options.getSyncedOptions().mainArm();
        if (entity != null) mainArm = entity.getMainArm();

        return mainArm.toString().toLowerCase();
    }
}
