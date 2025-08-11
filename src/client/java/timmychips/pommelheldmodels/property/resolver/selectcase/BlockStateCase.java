package timmychips.pommelheldmodels.property.resolver.selectcase;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.ItemStack;

public class BlockStateCase {

    public static String test(String block_state_property, ItemStack stack) {
        BlockStateComponent block_state = stack.get(DataComponentTypes.BLOCK_STATE);
        if (block_state == null) return null;

        return block_state.properties().get(block_state_property); // retrieves value from string
    }
}
