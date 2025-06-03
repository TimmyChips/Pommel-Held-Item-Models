package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;
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
    public static int tick = 80;
    public static boolean isUsingItem = false;
//    public static HashMap<PlayerEntity, ItemStack> player_lastUsedItem;

    public static float itemUsingLerp() {
        int tickMax = 20;
        float f = (float) useTicks / tickMax;
        if (useTicks > 0) useTicks -= 1;
        return f;
    }

    public static void tickTimer(LivingEntity entity) {
        if (entity.isPlayer() && ClientInitializer.player_usedItem.containsKey((PlayerEntity) entity)) {
            if (tick > 0) tick--;
        }
    }

    public static float player_usedItemTimer(LivingEntity livingEntity, ItemStack usableItem) {
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;
//        HashMap<PlayerEntity, ItemStack> player_lastUsedItemMap = ClientInitializer.player_usedItem;

        ItemStack lastUsedItem = ClientInitializer.player_usedItem.get((PlayerEntity) livingEntity);
//        if (tick > 0) tick--;
        if (tick == 0) lastUsedItem = ItemStack.EMPTY;

//        LOGGER.info("Owner " + livingEntity + "'s LAST Used Item: " + lastUsedItem + " and tick: " + tick);

        if (lastUsedItem != null) {
            if (lastUsedItem.isEmpty()) return 0.0F;
            return (lastUsedItem == livingEntity.getMainHandStack() || lastUsedItem == livingEntity.getOffHandStack()) ? 1.0F : 0.0F;
        }
        return 0.0F;

//        for (var p:player_lastUsedItemMap.entrySet()) {
////            ItemStack lastUsedItem = ItemStack.EMPTY;
////            lastUsedItem = p.getValue();
//            if (tick > 0) tick--;
////            if (tick == 0) lastUsedItem = ItemStack.EMPTY;
//            if (tick == 0) p.setValue(ItemStack.EMPTY);
//        }
//        LOGGER.info("LAST Used Item: " + String.valueOf(player_lastUsedItemMap) + " and tick: " + tick);
//
//        return 1.0F;
    }

    public static float player_useItemKey(LivingEntity livingEntity, ItemStack usableItem) {
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;

        ItemStack usedItem = ClientInitializer.player_usedItem.get((PlayerEntity) livingEntity);
        if (usedItem != null) {
            if (usedItem.isEmpty()) return 0.0F;
            return (usedItem == livingEntity.getMainHandStack() || usedItem == livingEntity.getOffHandStack()) ? 1.0F : 0.0F;
        }
        return 0.0F;
    }
}