package timmychips.pommelheldmodels;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import javax.swing.text.html.parser.Entity;
import java.util.HashSet;
import java.util.Set;

public class GroundItemSubmerged {
    boolean isSubmerged = false;
    ItemEntity itemEntity;
    ItemStack stack;
    public static Set<ItemStack> SUBMERGED_MAP = new HashSet<>();

    public void addItemEntity(ItemEntity itemEntity) {
        this.stack = itemEntity.getStack();
        this.isSubmerged = true;
        SUBMERGED_MAP.add(this.stack);
    }
}
