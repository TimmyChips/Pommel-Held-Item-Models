package timmychips.pommelheldmodels;

import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Initializes mod from main class method
		HeldModelManager.init();

		/* Example for declaring a single model and loading it with the ModelLoadingPlugin interface
			Identifier specific_model_example = Identifier.of("minecraft", "held_models/diamond_pickaxe");
				// Will register as "<minecraft:models/held_models/diamond_pickaxe>"
			ModelLoadingPlugin.register(plugin -> plugin.addModels(specific_model_example));
				// Registers model
		*/
	}
}