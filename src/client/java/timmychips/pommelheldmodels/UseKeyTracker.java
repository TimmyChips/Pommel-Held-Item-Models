package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ItemStack itemUsed = ItemStack.EMPTY;
    private static boolean useKeyPressed = false;
    public static HashMap<PlayerEntity, PlayerHeldItem> itemMap = new HashMap<>();

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
                    // Initialize player with item use data
                    itemMap.put(user, new PlayerHeldItem(itemUsed));
                }
            }
        });

        // Occurs at every world tick so frame rate is capped to ~20ticks/sec
        // Updates void methods
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            for (var player:world.getPlayers()) {
                UseKeyTracker.useTickInterval(player); // Tick timer for other (non-client) players to retain item usage
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
                            itemMap.put(sender, new PlayerHeldItem(payload.itemStack()));
                        }
                    }
                });
            }
        });
    }

    // Countdown tick timer
    // Since UseItemCallback event doesn't occur every tick, we have a countdown before we update that the other player is no longer using an item
    public static void useTickInterval(LivingEntity entity) {
        if (entity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) entity; // Cast LivingEntity to PlayerEntity

            if (itemMap.containsKey(player)) {
                int intervalTick = itemMap.get(player).checkInterval; // Gets current interval value

                if (intervalTick > 0) intervalTick--;
                if (intervalTick == 0) afterUseCooldown(player); // Does afterUseCooldown method when player stops using item
                else itemMap.get(player).checkInterval = intervalTick; // Update new interval value
            }
        }
    }

    public static void afterUseCooldown(PlayerEntity player) {
        float useTimer = itemMap.get(player).lastUsed;

        if (useTimer > 0F) {
            if (HeldItemPredicate.matchesItemInHand(player, itemMap.get(player).lastItem)) useTimer--; // Item being used is held in hand
            else useTimer = 0F; // Stops timer if player changes items from what they last used
        }
        if (useTimer == 0F) itemMap.remove(player);
        else itemMap.get(player).lastUsed = useTimer; // Update new cooldown value
    }

    // Item Predicate logic to set "is_using" predicate float based on some criteria
    public static float playerUseItemKey(LivingEntity livingEntity, ItemStack usableItem) {
        // Items that you can actually use (food, bow, shield, etc.)
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;

        // For non-usable items like pickaxes, blocks, materials, etc.
        PlayerEntity player = (PlayerEntity) livingEntity;

        float returnFloat = 0.0F;
        if (itemMap.containsKey(player)) {
            ItemStack lastItem = itemMap.get(player).lastItem;

            if (lastItem != null) {
                returnFloat = itemMap.get(player).lastUsed / 20.0F; // Get normalized value of last used timer from 0 to 1 for that player
            }
        }
        return returnFloat;
    }
}