package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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
    public static int useTicks = 0;

    // When client player/user presses the use key; occurs every client tick
    public static void clientUseKey() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                PlayerEntity user = MinecraftClient.getInstance().player;
                KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
                useKeyPressed = useKey.isPressed();

                if (user != null) itemUsed = user.getMainHandStack().isEmpty() ? user.getOffHandStack() : user.getMainHandStack(); // gets main or offhand ItemStack

                // Adds or removes the client user and the item used to HashMap when pressing the use key or not
                if (useKeyPressed) player_usedItem.put(user, itemUsed);
                if (!useKeyPressed) player_usedItem.remove(user);
            }
        });
    }

    // Event that sends packet to server when client player/user presses right click
    public static void eventUseKeyPacket() {
        UseItemCallback.EVENT.register((PlayerEntity user, World world, net.minecraft.util.Hand hand) -> {
            if (!world.isClient) {
//                LOGGER.info("Used Item");
                UUID playerUuid = user.getUuid();
                ItemStack sendItemUsed = user.getStackInHand(hand);

                UseKeyPayload payload = new UseKeyPayload(playerUuid, sendItemUsed, true);

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
                            UseKeyTracker.player_useCooldown.put(sender, 70); // Adds sender to second HashMap with a tick countdown timer
//                            LOGGER.info("Other player: " + sender + " using: " + payload.itemStack());
                        }
                    }
                });
            }
        });
    }

    public static float itemUsingLerp() {
        int tickMax = 20;
        float f = (float) useTicks / tickMax;
        if (useTicks > 0) useTicks -= 1;
        return f;
    }

    // Countdown tick timer
    // Since UseItemCallback event doesn't occur every tick, we have a countdown before we update that the other player is no longer using an item
    public static void tickTimer(LivingEntity entity) {
        if (entity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) entity; // Cast LivingEntity to PlayerEntity

            if (player_useCooldown.containsKey(player)) { // Gets player and their current countdown tick
                int p_tick = player_useCooldown.get(player);
//                LOGGER.info(String.valueOf(p_tick));
                if (p_tick > 0) p_tick--; // Get and subtract the player's tick

                if (p_tick == 0) { // Removes the player from both HashMaps when countdown reaches 0; item no longer being used
                    UseKeyTracker.player_useCooldown.remove(player);
                    UseKeyTracker.player_usedItem.remove(player);
                }
                else player_useCooldown.replace(player, p_tick); // Updates tick timer to new, subtracted value
            }
        }
    }

    // Item Predicate logic to set "is_using" predicate float based on some criteria
    public static float player_useItemKey(LivingEntity livingEntity, ItemStack usableItem) {
        // Items that you can actually use (food, bow, shield, etc.)
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;

        // Get items that the player used that may be un-interactable items (pickaxes, materials)
        ItemStack usedItem = player_usedItem.get((PlayerEntity) livingEntity);

        if (usedItem != null) {
            if (usedItem.isEmpty()) return 0.0F;
            return 1.0F;

        }
        return 0.0F;
    }
}