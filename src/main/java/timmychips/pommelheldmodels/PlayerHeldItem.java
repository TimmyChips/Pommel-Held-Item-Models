package timmychips.pommelheldmodels;

import net.minecraft.item.ItemStack;

public class PlayerHeldItem {
    ItemStack lastItem;
    float isUsing = 20.0F;
    int checkInterval = 4;

//    public void put(ItemStack stack, float afterUseCooldown, int tickIntervalCheck) {
//        this.lastItem = stack;
//        this.isUsing = afterUseCooldown;
//        this.checkInterval = tickIntervalCheck;
//    }

    public PlayerHeldItem(ItemStack stack) {
        this.lastItem = stack;
    }
}
