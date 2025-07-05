package timmychips.pommelheldmodels.resolver.condition;

import com.mojang.logging.LogUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;


public class CustomModelDataBool {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> WARNED_MODELS = new HashSet<>();

    public static Boolean test(ItemStack stack) {

        // Warning that custom_model_data is only an integer in versions below 1.21.4
        String key = stack.getItem().toString() + "|" + "minecraft:custom_model_data";
        if (WARNED_MODELS.add(key)) LOGGER.warn("Unable to read 'custom_model_data' for type: 'minecraft:condition' since component is an integer in this version. Defaulting to be true if component is present on item.");

        // Will still check if custom_model_data component is there
        var custom_model_data = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return custom_model_data != null;
    }
}
