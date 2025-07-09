package timmychips.pommelheldmodels.resolver.selectcase;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;

public class CustomModelDataCase {

    public static String test(ItemStack stack) {
        CustomModelDataComponent custom_model_data = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return custom_model_data != null ? String.valueOf(custom_model_data.value()) : null;
    }
}
