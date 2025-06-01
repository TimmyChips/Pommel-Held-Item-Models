package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ItemStack itemUsed = null;
    public static int useTicks = 0;

    public static float itemUsingLerp() {
        int tickMax = 20;
        float f = (float) useTicks / tickMax;
        if (useTicks > 0) useTicks -= 1;
        return f;
    }
}