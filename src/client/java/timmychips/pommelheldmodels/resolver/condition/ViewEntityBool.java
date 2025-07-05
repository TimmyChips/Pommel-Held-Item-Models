package timmychips.pommelheldmodels.resolver.condition;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ViewEntityBool {
    public static Boolean test(LivingEntity entity) {
        MinecraftClient client =MinecraftClient.getInstance();
        Entity entity1 = client.getCameraEntity(); // get entity player is spectating in spectator mode
        return entity1 != null ? entity == entity1 : entity == client.player; // return true if player is spectating entity or is local client player
    }
}
