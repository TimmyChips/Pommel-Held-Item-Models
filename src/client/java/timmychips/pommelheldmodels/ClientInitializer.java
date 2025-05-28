package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class ClientInitializer implements ClientModInitializer {

	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Register model item predicate
			// Any .json model file with the <"pommel:is_held": 1.0> item predicate will override and render the item
			// with the specified held model file
		HeldItemPredicate.registerHeldModelPredicate();

	}
}