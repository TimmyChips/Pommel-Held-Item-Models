package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class RegisterItemModels implements ModelLoadingPlugin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        // Collect all model dependencies from your custom registry
        Set<Identifier> modelIds = ItemModelRegistry.getAllModelDependencies();
        modelIds.forEach(id -> LOGGER.info("[Pommel] Registering model dependency: {}", id));

        // Add them all in one go
        pluginContext.addModels(modelIds);
    }
}
