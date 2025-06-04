package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Hash;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

public class ClientInitializer implements ClientModInitializer {

	private static final Logger LOGGER = LogUtils.getLogger();
	public static PlayerEntity player_ent;
	public static HashMap<PlayerEntity, ItemStack> player_usedItem = new HashMap<PlayerEntity, ItemStack>();

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Register model item predicate
			// Any .json model file with the <"pommel:is_held": 1.0> item predicate will override and render the item
			// with the specified held model file
		LOGGER.info("Hello There");
		HeldItemPredicate.registerHeldModelPredicate();

		LOGGER.info("!!! Registering UseItemCallback Event");
		UseKeyTracker.clientUseKey();
		UseKeyTracker.eventUseKeyPacket();
	}
}