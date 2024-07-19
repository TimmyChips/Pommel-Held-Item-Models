package timmychips.pommelheldmodels;

import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Register model item predicate
			// Any .json model file with the <"pommel:is_held": 1.0> item predicate will override and render the item
			// with the specified held model file
		HeldItemPredicate.registerHeldModelPredicate();

	}
}