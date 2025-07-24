package timmychips.pommelheldmodels;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ServerInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        PommelNetworking.registerPayloads();
        PommelNetworking.useKeyGlobalReceiver();

        ItemTest.initItem();
    }
}
