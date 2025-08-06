package timmychips.pommelheldmodels;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class PommelNetworking {
    public static final Identifier USE_KEY_C2S_ID = new Identifier("pommel", "use_key_c2s");
    public static final Identifier USE_KEY_S2C_ID = new Identifier("pommel", "use_key_s2c");

    public static void useKeyGlobalReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(USE_KEY_C2S_ID, (server, player, handler, buf, responseSender) -> {
            UUID senderUuid = buf.readUuid();
            ItemStack stack = buf.readItemStack();
            boolean isUsing = buf.readBoolean();

            System.out.println("[Pommel] Player " + senderUuid + " is using: " + stack + ", lastUsed: " + isUsing);

            // Now send to other players
            PacketByteBuf sendBuf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
            sendBuf.writeUuid(senderUuid);
            sendBuf.writeItemStack(stack);
            sendBuf.writeBoolean(isUsing);

            for (ServerPlayerEntity otherPlayer : player.server.getPlayerManager().getPlayerList()) {
                if (!otherPlayer.getUuid().equals(senderUuid)) {
                    ServerPlayNetworking.send(otherPlayer, USE_KEY_S2C_ID, sendBuf);
                }
            }
        });
    }
}
