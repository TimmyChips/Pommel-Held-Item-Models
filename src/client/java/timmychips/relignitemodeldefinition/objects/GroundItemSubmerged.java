package timmychips.relignitemodeldefinition.objects;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class GroundItemSubmerged {
    ItemStack stack;
    public static final Set<ItemStack> SUBMERGED_MAP = new HashSet<>();

    public void addItemEntity(ItemEntity itemEntity) {
        this.stack = itemEntity.getStack();
        SUBMERGED_MAP.add(this.stack);
    }
}
