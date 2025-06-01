package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class ClientInitializer implements ClientModInitializer {

	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Register model item predicate
			// Any .json model file with the <"pommel:is_held": 1.0> item predicate will override and render the item
			// with the specified held model file

		LOGGER.info("Hello There");
		HeldItemPredicate.registerHeldModelPredicate();

		LOGGER.info("!!! Registering UseItemCallback Event");
//		UseItemCallback.EVENT.register((PlayerEntity player, World world, net.minecraft.util.Hand hand) -> {
//			if (world.isClient) {
//				LOGGER.info("[Pommel] Used item: " + player.getStackInHand(hand));
//			}
//			return TypedActionResult.pass(player.getStackInHand(hand));
//		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world != null) {
				KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
				HeldItemPredicate.isUsingItem = useKey.isPressed();
				LOGGER.info(String.valueOf(HeldItemPredicate.isUsingItem));
			}
		});

	}
}