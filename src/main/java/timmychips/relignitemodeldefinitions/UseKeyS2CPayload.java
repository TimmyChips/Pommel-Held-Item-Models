package timmychips.relignitemodeldefinitions;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record UseKeyS2CPayload(UUID playerUuid, ItemStack itemStack, boolean isUsing) implements CustomPayload {
    public static final Identifier ID = Identifier.of("pommel", "use_key_sync");
    public static final CustomPayload.Id<UseKeyS2CPayload> PACKET_ID = new CustomPayload.Id<>(ID);

    public static final PacketCodec<RegistryByteBuf, UseKeyS2CPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, UseKeyS2CPayload::playerUuid,
            ItemStack.PACKET_CODEC, UseKeyS2CPayload::itemStack,
            PacketCodecs.BOOL, UseKeyS2CPayload::isUsing,
            UseKeyS2CPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return PACKET_ID; }
}
