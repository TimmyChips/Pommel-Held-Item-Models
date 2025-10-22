package timmychips.relignitemodeldefinition.objects;

import net.minecraft.item.ItemStack;

public class PlayerHeldItem {
    public final ItemStack lastItem;
    public float lastUsed = 20.0F;
    public int checkInterval = 6;

    public PlayerHeldItem(ItemStack stack) {
        this.lastItem = stack;
    }
}
