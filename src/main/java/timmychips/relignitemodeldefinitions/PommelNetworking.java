package timmychips.relignitemodeldefinitions;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class PommelNetworking {
    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(UseKeyC2SPayload.PACKET_ID, UseKeyC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UseKeyS2CPayload.PACKET_ID, UseKeyS2CPayload.CODEC);
    }

    public static void useKeyGlobalReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(UseKeyC2SPayload.PACKET_ID, (payload, context) -> {
            ServerPlayerEntity sender = context.player();
            UUID senderUuid = payload.playerUuid();
            ItemStack stack = payload.itemStack();
            boolean isUsing = payload.isUsing();

            UseKeyS2CPayload broadcastPayload = new UseKeyS2CPayload(senderUuid, stack, isUsing);

            for (ServerPlayerEntity player : sender.server.getPlayerManager().getPlayerList()) {
                if (!player.getUuid().equals(senderUuid)) {
                    ServerPlayNetworking.send(player, broadcastPayload);
                }
            }
        });
    }
}
