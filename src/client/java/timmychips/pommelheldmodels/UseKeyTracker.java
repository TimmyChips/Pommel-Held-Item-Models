package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.HashMap;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ItemStack itemUsed = null;
    public static int useTicks = 0;
    public static int tick = 70;
//    public static HashMap<PlayerEntity, ItemStack> player_lastUsedItem;

    public static float itemUsingLerp() {
        int tickMax = 20;
        float f = (float) useTicks / tickMax;
        if (useTicks > 0) useTicks -= 1;
        return f;
    }

    public static HashMap<PlayerEntity, ItemStack> player_usedItemTimer() {
        HashMap<PlayerEntity, ItemStack> player_lastUsedItemMap = ClientInitializer.player_usedItem;
        for (var p:player_lastUsedItemMap.entrySet()) {
//            ItemStack lastUsedItem = ItemStack.EMPTY;
//            lastUsedItem = p.getValue();
            if (tick > 0) tick--;
//            if (tick == 0) lastUsedItem = ItemStack.EMPTY;
            if (tick == 0) p.setValue(ItemStack.EMPTY);
        }
        LOGGER.info("LAST Used Item: " + String.valueOf(player_lastUsedItemMap) + " and tick: " + tick);
        return player_lastUsedItemMap;
    }
}