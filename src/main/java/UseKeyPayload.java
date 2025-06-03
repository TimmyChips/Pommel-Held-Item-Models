import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record UseKeyPayload(UUID playerUuid, ItemStack itemStack, boolean isUsing) implements CustomPayload {
    public static final Identifier ID = Identifier.of("pommel", "use_key");
    public static final CustomPayload.Id<UseKeyPayload> PACKET_ID = new CustomPayload.Id<>(ID);

    public static final PacketCodec<RegistryByteBuf, UseKeyPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, UseKeyPayload::playerUuid,
            ItemStack.PACKET_CODEC, UseKeyPayload::itemStack,
            PacketCodecs.BOOL, UseKeyPayload::isUsing,
            UseKeyPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
