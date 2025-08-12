package timmychips.pommelheldmodels.property.resolver.selectcase;

import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.property.handler.SelectPropertyHandler;
import timmychips.pommelheldmodels.property.type.SelectDefinition;

public class ContextDimensionCase implements SelectPropertyHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO
    //  Parse Case string into Identifier, not sure how though
    //  Maybe add Codec Case Value entry to this class that sets value type to Identifier

    public static String test(LivingEntity entity) {
        return entity != null ? entity.getEntityWorld().getDimensionEntry().getIdAsString() : null;
    }

    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        if (entity == null) return null;
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        return clientWorld != null ? clientWorld.getRegistryKey().getValue().toString() : null;
    }
}
