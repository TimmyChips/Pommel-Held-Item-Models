package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ItemStack itemUsed = ItemStack.EMPTY;
    private static boolean useKeyPressed = false;
    public static HashMap<PlayerEntity, ItemStack> player_usedItem = new HashMap<PlayerEntity, ItemStack>();
    public static HashMap<PlayerEntity, Integer> player_useCooldown = new HashMap<PlayerEntity, Integer>();
    public static HashMap<PlayerEntity, Float> player_releaseCountdown = new HashMap<PlayerEntity, Float>();

    // When client player/user presses the use key; occurs every client tick
    public static void clientUseKey() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                PlayerEntity user = MinecraftClient.getInstance().player;
                KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
                useKeyPressed = useKey.isPressed();

                if (user != null) itemUsed = user.getMainHandStack().isEmpty() ? user.getOffHandStack() : user.getMainHandStack(); // gets main or offhand ItemStack

                // Adds or removes the client user and the item used to HashMap when pressing the use key or not
                if (useKeyPressed) {
                    player_usedItem.put(user, itemUsed);
                    UseKeyTracker.player_useCooldown.put(user, 4);
                    UseKeyTracker.player_releaseCountdown.put(user, 20.0F);
                }
                if (!useKeyPressed) player_usedItem.remove(user);
            }
        });

        // Occurs at every world tick so frame rate is capped to ~20ticks/sec
        // Updates void methods
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            for (var player:world.getPlayers()) {
                UseKeyTracker.playerUsedItemTickTimer(player); // Tick timer for other (non-client) players to retain item usage
                UseKeyTracker.playerReleaseCountdown(player); // Release countdown
            }
        });
    }

    // Event that sends packet to server when client player/user presses right click
    public static void eventUseKeyPacket() {
        UseItemCallback.EVENT.register((PlayerEntity user, World world, net.minecraft.util.Hand hand) -> {
            if (!world.isClient) {
                UUID playerUuid = user.getUuid();
                ItemStack sendItemUsed = user.getStackInHand(hand);

                ItemStack sendItemUsed2 = sendItemUsed.copy(); // Create new item and remove enchantments as game crashes when trying to send enchanted item data
                sendItemUsed2.remove(DataComponentTypes.ENCHANTMENTS);
                sendItemUsed2.remove(DataComponentTypes.STORED_ENCHANTMENTS);

                UseKeyC2SPayload payload = new UseKeyC2SPayload(playerUuid, sendItemUsed2, true);

                ClientPlayNetworking.send(payload); // Sends payload to server
            }

			return TypedActionResult.pass(user.getStackInHand(hand)); // Pass to return that we did the event
		});
    }

    // Receives packet of other player pressing the use key from the server for other clients
    public static void receiveUseKeyPacket() {
        ClientPlayNetworking.registerGlobalReceiver(UseKeyS2CPayload.PACKET_ID, (payload, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                client.execute(() -> {
                    PlayerEntity sender = client.world.getPlayerByUuid(payload.playerUuid());
                    if (sender != null) {
                        if (payload.isUsing()) {
                            UseKeyTracker.player_usedItem.put(sender, payload.itemStack()); // Add the sender player and their item to HashMap
                            UseKeyTracker.player_useCooldown.put(sender, 4); // Adds sender to second HashMap with a tick countdown timer
                            UseKeyTracker.player_releaseCountdown.put(sender, 20.0F); // Adds sender to a countdown that'll start when no longer using item
                        }
                    }
                });
            }
        });
    }

    // Countdown tick timer
    // Since UseItemCallback event doesn't occur every tick, we have a countdown before we update that the other player is no longer using an item
    public static void playerUsedItemTickTimer(LivingEntity entity) {
        if (entity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) entity; // Cast LivingEntity to PlayerEntity

            if (player_useCooldown.containsKey(player)) { // Gets player and their current countdown tick
                int p_tick = player_useCooldown.get(player);
                if (p_tick > 0) p_tick--; // Get and subtract the player's tick

                if (p_tick == 0) { // Removes the player from both HashMaps when countdown reaches 0; item no longer being used
                    UseKeyTracker.player_useCooldown.remove(player);
                    UseKeyTracker.player_usedItem.remove(player);

                    // method call
                }
                else player_useCooldown.replace(player, p_tick); // Updates tick timer to new, subtracted value
            }
        }
    }

    public static void playerReleaseCountdown(LivingEntity entity) {
        if (entity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) entity;

            if (player_releaseCountdown.containsKey(player)) {
                float p_countdown = player_releaseCountdown.get(player);

                if (!player_usedItem.containsKey(player) || !player_useCooldown.containsKey(player)) {

                    LOGGER.info(String.valueOf(player_releaseCountdown.get(player)));
                    if (p_countdown > 0F) p_countdown--;

                    if (p_countdown == 0F) {
                        UseKeyTracker.player_releaseCountdown.remove(player);
                    } else player_releaseCountdown.replace(player, p_countdown);
                }
            }
        }
    }

    // Item Predicate logic to set "is_using" predicate float based on some criteria
    public static float playerUseItemKey(LivingEntity livingEntity, ItemStack usableItem) {
        // Items that you can actually use (food, bow, shield, etc.)
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;

        PlayerEntity player = (PlayerEntity) livingEntity;
        // Get items that the player used that may be un-interactable items (pickaxes, materials)
        ItemStack usedItem = player_usedItem.get(player);

        ItemStack usedItem2 = ItemStack.EMPTY; // copies usedItem since it's removed immediately from HashMap when not using item
        if (usedItem != null) usedItem2 = usedItem.copy();
        float cooldownTick = 0.0F;
        // TODO:
        //  Possible to refactor the cooldown hashmap into the other? Should stay separate?
        //  Also need to reset/fix cooldown when you swap items then back to used item; rn it doesn't reset
        if (player_releaseCountdown.get(player) != null) cooldownTick = player_releaseCountdown.get(player); // get cooldown from map

        if (usedItem2 != null) {
            cooldownTick /= 18.0F; // normalizes range from 0.0 to ~1.0
            return cooldownTick;
        }
        return 0.0F;
    }
}