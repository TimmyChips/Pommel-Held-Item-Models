package timmychips.pommelheldmodels;

import net.minecraft.item.ItemStack;

public class PlayerHeldItem {
    ItemStack lastItem;
    float lastUsed = 20.0F;
    int checkInterval = 4;

    public PlayerHeldItem(ItemStack stack) {
        this.lastItem = stack;
    }
}
