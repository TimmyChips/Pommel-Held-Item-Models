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

    public static void register() {
        LOGGER.info("!!!!! REGISTERED USE KEY TRACKER");
        UseItemCallback.EVENT.register((PlayerEntity player, World world, net.minecraft.util.Hand hand) -> {
            if (world.isClient) {
                itemUsed = player.getStackInHand(hand);
                System.out.println("[Pommel] Used item: " + player.getStackInHand(hand));
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }

    public static ItemStack getItemUsed() {
        ItemStack itemWasUsed = itemUsed;
        itemUsed = null;
        return itemWasUsed;
    }
}