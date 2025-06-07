package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;

import java.util.HashMap;

public class ClientInitializer implements ClientModInitializer {

	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Register model item predicate
			// Any .json model file with the <"pommel:is_held": 1.0> item predicate will override and render the item
			// with the specified held model file
		HeldItemPredicate.registerHeldModelPredicate();

		// Register methods using items for the is_using predicate
		UseKeyTracker.receiveUseKeyPacket();
		UseKeyTracker.clientUseKey();
		UseKeyTracker.eventUseKeyPacket();
	}
}