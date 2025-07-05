package timmychips.pommelheldmodels.resolver.condition;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class CarriedBool {
    public static Boolean test(LivingEntity entity, ItemStack stack) {
        boolean carrying_item = false;
        ClientPlayerEntity clientPlayer = null;

        // Try to use the rendering entity, or fallback to the client player
        if (entity instanceof ClientPlayerEntity player) {
            clientPlayer = player;
        } else if (MinecraftClient.getInstance().player != null) {
            clientPlayer = MinecraftClient.getInstance().player;
        }

        if (clientPlayer != null) {
            if (clientPlayer.currentScreenHandler.getCursorStack() == stack) { // get item from cursor
                carrying_item = true;
            }
        }

        return carrying_item;
    }
}
