package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ItemStack itemUsed = null;
    private static int useTicks = 0;
    private static int tickMax = 20;

//    public static void register() {
//        LOGGER.info("!!!!! REGISTERED USE KEY TRACKER");
//        UseItemCallback.EVENT.register((PlayerEntity player, World world, net.minecraft.util.Hand hand) -> {
//            if (world.isClient) {
//                useTicks = 20;
//                System.out.println("[Pommel] Used item: " + player.getStackInHand(hand));
//            }
//            return TypedActionResult.pass(player.getStackInHand(hand));
//        });
//    }

    public static float getItemUse() {
        float f = (float) useTicks / tickMax;
        if (useTicks > 0) useTicks -= 1;
        return f;
    }
}