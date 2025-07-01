package timmychips.pommelheldmodels.helper;

import com.mojang.logging.LogUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class StringIDHelper {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static String parseStringtoID(String inputString, ItemStack stack) {

        Identifier inputId = Identifier.tryParse(inputString);
        if (inputId != null) {
//            LOGGER.info(inputId.toString());
            return inputId.toString();
        }

        // TODO
        //  Always parses string to 'minecraft', such as 'minecraft:this_isn't_valid'
        //  LOGGER.warn("[Pommel] Couldn't parse item model '{}' from pack 'PACK': Unknown element id: '{}'", stack.getItem(), inputString);
        return inputString;
    }
}
