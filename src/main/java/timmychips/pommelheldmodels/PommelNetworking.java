package timmychips.pommelheldmodels;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PommelNetworking {
    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(UseKeyPayload.PACKET_ID, UseKeyPayload.CODEC);
    }
}
