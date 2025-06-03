package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Hash;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.HashMap;

public class ClientInitializer implements ClientModInitializer {

	private static final Logger LOGGER = LogUtils.getLogger();
	public static PlayerEntity player_ent;
	public static HashMap<PlayerEntity, ItemStack> player_usedItem = new HashMap<PlayerEntity, ItemStack>();
	public static final Identifier USE_KEY_PACKET_ID = Identifier.of("pommel", "use_item");

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
////				player_ent = player;
////				LOGGER.info(String.valueOf(player.getId()));
//				player_usedItem.put(player, player.getStackInHand(hand));
//				UseKeyTracker.tick = 80;
//
//				LOGGER.info("CURRENT Used Item: " + String.valueOf(player_usedItem));
//			}
//			return TypedActionResult.pass(player.getStackInHand(hand));
//		});

//		ClientTickEvents.END_CLIENT_TICK.register(client -> {
//			if (client.world != null) {
//				KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
//				HeldItemPredicate.isUsingItem = useKey.isPressed();
//				if (useKey.isPressed()) UseKeyTracker.useTicks = 20;
//				HeldItemPredicate.isUsingItemFloat = UseKeyTracker.itemUsingLerp();
//				LOGGER.info(String.valueOf(HeldItemPredicate.isUsingItem));
//			}
//		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world != null) {
				PlayerEntity player = MinecraftClient.getInstance().player;
				KeyBinding useKey = MinecraftClient.getInstance().options.useKey;

				if (useKey.isPressed() && player != null) {
					ItemStack stack = player.getMainHandStack().isEmpty() ? player.getOffHandStack() : player.getMainHandStack();
					player_usedItem.put(player, stack);
				}

				if (!useKey.isPressed()) player_usedItem.remove(player);

				LOGGER.info("CURRENT Used Item: " + String.valueOf(player_usedItem));
			}
		});
	}
}