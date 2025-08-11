package timmychips.pommelheldmodels.property.resolver.selectcase;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.LivingEntity;
import org.slf4j.Logger;

public class ContextDimensionCase {

    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO
    //  Parse Case string into Identifier, not sure how though

    public static String test(LivingEntity entity) {
        return entity != null ? entity.getEntityWorld().getDimensionEntry().getIdAsString() : null;
    }
}
