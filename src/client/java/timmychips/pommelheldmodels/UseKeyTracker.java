package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
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
    public static int useTicks = 0;
    public static int tick = 80;
    public static boolean isUsingItem = false;

    public static void clientUseKey() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                PlayerEntity user = MinecraftClient.getInstance().player;
                KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
                useKeyPressed = useKey.isPressed();

                if (user != null) itemUsed = user.getMainHandStack().isEmpty() ? user.getOffHandStack() : user.getMainHandStack();

                if (useKeyPressed) player_usedItem.put(user, itemUsed);
                if (!useKeyPressed) player_usedItem.remove(user);
            }
        });
    }

    public static void eventUseKeyPacket() {
        UseItemCallback.EVENT.register((PlayerEntity user, World world, net.minecraft.util.Hand hand) -> {
			if (world.isClient) {
                return TypedActionResult.pass(user.getStackInHand(hand));
			}

            LOGGER.info("Used Item");
            UUID playerUuid = user.getUuid();

            UseKeyPayload payload = new UseKeyPayload(playerUuid, itemUsed, useKeyPressed);
            ClientPlayNetworking.send(payload);

			return TypedActionResult.pass(user.getStackInHand(hand));
		});
    }

    public static void recieveUseKeyPacket() {
        ClientPlayNetworking.registerGlobalReceiver(UseKeyS2CPayload.PACKET_ID, (payload, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                client.execute(() -> {
                    PlayerEntity sender = client.world.getPlayerByUuid(payload.playerUuid());
                    if (sender != null) {
                        if (payload.isUsing()) {
                            UseKeyTracker.player_usedItem.put(sender, payload.itemStack());
                        } else {
                            UseKeyTracker.player_usedItem.remove(sender);
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

//    public static void tickTimer(LivingEntity entity) {
//        if (entity.isPlayer() && player_usedItem.containsKey((PlayerEntity) entity)) {
//            if (tick > 0) tick--;
//        }
//    }
//
//    public static float player_usedItemTimer(LivingEntity livingEntity, ItemStack usableItem) {
//        if (!livingEntity.isPlayer()) return 0.0F;
//        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;
//
//        ItemStack lastUsedItem = player_usedItem.get((PlayerEntity) livingEntity);
//        if (tick == 0) lastUsedItem = ItemStack.EMPTY;
//
//        if (lastUsedItem != null) {
//            if (lastUsedItem.isEmpty()) return 0.0F;
//            return (lastUsedItem == livingEntity.getMainHandStack() || lastUsedItem == livingEntity.getOffHandStack()) ? 1.0F : 0.0F;
//        }
//        return 0.0F;
//    }

    public static float player_useItemKey(LivingEntity livingEntity, ItemStack usableItem) {
        if (!livingEntity.isPlayer()) return 0.0F;
        if (livingEntity.isUsingItem() && livingEntity.getActiveItem() == usableItem) return 1.0F;

        ItemStack usedItem = player_usedItem.get((PlayerEntity) livingEntity);
        if (usedItem != null) {
            if (usedItem.isEmpty()) return 0.0F;
            return (usedItem == livingEntity.getMainHandStack() || usedItem == livingEntity.getOffHandStack()) ? 1.0F : 0.0F;
        }
        return 0.0F;
    }
}