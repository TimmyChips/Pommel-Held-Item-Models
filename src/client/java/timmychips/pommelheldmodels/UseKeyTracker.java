package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register() {
        System.out.println("REGISTERED USE KEY TRACKER");
        LOGGER.info("REGISTERED USE KEY TRACKER");
        UseItemCallback.EVENT.register((PlayerEntity player, World world, net.minecraft.util.Hand hand) -> {
            if (world.isClient) {
                System.out.println("[Pommel] Used item: " + player.getStackInHand(hand));
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }
}