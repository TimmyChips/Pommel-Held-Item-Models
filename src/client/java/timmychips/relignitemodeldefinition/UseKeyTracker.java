package timmychips.relignitemodeldefinition;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;
import timmychips.relignitemodeldefinition.objects.PlayerHeldItem;

import java.util.HashMap;
import java.util.UUID;

public class UseKeyTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ItemStack itemUsed = ItemStack.EMPTY;
    private static boolean useKeyPressed = false;
    public static final HashMap<PlayerEntity, PlayerHeldItem> itemMap = new HashMap<>();

    // When client player/user presses the use key; occurs every client tick
    public static void clientUseKey() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                PlayerEntity user = MinecraftClient.getInstance().player;
                KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
                useKeyPressed = useKey.isPressed();

                if (user != null) itemUsed = user.getMainHandStack().isEmpty() ? user.getOffHandStack() : user.getMainHandStack(); // gets main or offhand ItemStack

                // Adds or removes the client user and the item used to HashMap when pressing the use key or not
                if (useKeyPressed && !itemUsed.isEmpty()) {
                    // Initialize player with item use data
                    ItemStack defaultStack = itemUsed.getItem().getDefaultStack();
                    itemMap.put(user, new PlayerHeldItem(defaultStack));
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

                // Get the base item from user and convert to default stack to avoid component map crashes
                Item itemUsed = user.getStackInHand(hand).getItem();
                ItemStack defaultStack = itemUsed.getDefaultStack();

                if (!defaultStack.isEmpty()) {
                    UseKeyC2SPayload payload = new UseKeyC2SPayload(playerUuid, defaultStack, true);
                    ClientPlayNetworking.send(payload); // Sends payload to server
                }
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
                            itemMap.put(sender, new PlayerHeldItem(payload.itemStack().copy()));
                        }
                    }
                });
            }
        });
    }

    // Countdown tick timer
    // Since UseItemCallback event doesn't occur every tick, we have a countdown before we update that the other player is no longer using an item
    public static void useTickInterval(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
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
            if (matchesItemInHand(player, itemMap.get(player).lastItem)) useTimer--; // Item being used is held in hand
            else useTimer = 0F; // Stops timer if player changes items from what they last used
        }
        if (useTimer == 0F) itemMap.remove(player);
        else itemMap.get(player).lastUsed = useTimer; // Update new cooldown value
    }

    // Returns if the currently rendered ItemStack matches what the player is holding
    // Intended for the client player, as to prevent non-selected items to not have their models change
    // Only the actively selected item will change item models
    // TODO Merge/Cleanup with matchesItemInHand method in HeldItemPredicate.java
    //  Currently only changes player's main hand item model if two different items are in main/offhand at same time; Fix?
    //  Also possibly clean/split this class up into other class(es)
    private static boolean clientHasItemSelected(LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof ClientPlayerEntity clientPlayer) {
//            Hand hand = clientPlayer.getActiveHand();
//            ItemStack currentStack = clientPlayer.getStackInHand(hand); // Only actually does it for player's main hand :(

            ItemStack currentStack = clientPlayer.getMainHandStack().isEmpty() ? clientPlayer.getOffHandStack() : clientPlayer.getMainHandStack();

            return ItemStack.areEqual(currentStack,stack);
        }
        return true;
    }

    // Item Predicate logic to set "is_using" predicate float based on some criteria
    public static float playerUseItemKey(LivingEntity livingEntity, ItemStack usableItem) {
        // Items that you can actually use (food, bow, shield, etc.)
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && ItemStack.areEqual(livingEntity.getActiveItem(), usableItem)) return 1.0F;

        // For non-usable items like pickaxes, blocks, materials, etc.
        PlayerEntity player = (PlayerEntity) livingEntity;

        float returnFloat = 0.0F;
        if (itemMap.containsKey(player)) {
            ItemStack lastItem = itemMap.get(player).lastItem;

            if (!clientHasItemSelected(player, usableItem)) return 0.0F; // If player is client and not has used item selected, return 0F

            if (lastItem != null) {
                returnFloat = itemMap.get(player).lastUsed / 18.0F; // Get normalized value of last used timer from 0 to 1 for that player
            }
        }
        return returnFloat;
    }

    public static boolean matchesItemInHand(LivingEntity entity, ItemStack stack) {
        ItemStack currentItem = entity.getMainHandStack().isEmpty() ? entity.getOffHandStack() : entity.getMainHandStack();
        return stack.toString().equals(currentItem.toString());
    }
}