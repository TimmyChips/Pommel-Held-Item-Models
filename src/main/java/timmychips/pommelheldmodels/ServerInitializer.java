package timmychips.pommelheldmodels;

import net.fabricmc.api.ModInitializer;

public class ServerInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        PommelNetworking.registerPayloads();
        PommelNetworking.useKeyGlobalReceiver();
    }
}
