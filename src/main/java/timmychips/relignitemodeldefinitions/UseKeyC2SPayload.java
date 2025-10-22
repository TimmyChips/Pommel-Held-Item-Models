package timmychips.relignitemodeldefinitions;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record UseKeyC2SPayload(UUID playerUuid, ItemStack itemStack, boolean isUsing) implements CustomPayload {
    public static final Identifier ID = Identifier.of("pommel", "use_key");
    public static final CustomPayload.Id<UseKeyC2SPayload> PACKET_ID = new CustomPayload.Id<>(ID);

    public static final PacketCodec<RegistryByteBuf, UseKeyC2SPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, UseKeyC2SPayload::playerUuid,
            ItemStack.PACKET_CODEC, UseKeyC2SPayload::itemStack,
            PacketCodecs.BOOL, UseKeyC2SPayload::isUsing,
            UseKeyC2SPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
