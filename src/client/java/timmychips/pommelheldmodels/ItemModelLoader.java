package timmychips.pommelheldmodels;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static timmychips.pommelheldmodels.ClientInitializer.LOGGER;

public class ItemModelLoader {

    private static final Map<Identifier, ItemModelDefinitionCodec.ItemModelDefinition> DEFINITION_MAP = new HashMap<>();

    public static void register(Identifier id, ItemModelDefinitionCodec.ItemModelDefinition def) {
        DEFINITION_MAP.put(id, def);
    }

    public static ItemModelDefinitionCodec.ItemModelDefinition getDefinition(Identifier id) {
        return DEFINITION_MAP.get(id);
    }

    public static void loadItemModelDefinition(Identifier id) {
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        Identifier path = Identifier.of(id.getNamespace(), "items/" + id.getPath() + ".json");
        LOGGER.info("[POMMEL] PATH: " + String.valueOf(path));

        manager.getResource(Identifier.of("minecraft", "items/" + id.getPath() + ".json")).ifPresentOrElse(resource -> {
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                JsonElement json = JsonParser.parseReader(reader);

                var result = ItemModelDefinitionCodec.DEFINITION_CODEC.decode(JsonOps.INSTANCE, json);
                result.result().ifPresentOrElse(
                        pair -> {
                            ItemModelDefinitionCodec.ItemModelDefinition definition = pair.getFirst();
                            System.out.println("[Pommel] Loaded model definition for: " + id + " = " + definition);
                            // TODO: store or apply the definition
                        },
                        () -> System.err.println("[Pommel] Failed to decode model definition for: " + id)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, () -> {
            System.err.println("[Pommel] Could not find model definition resource for: " + id);
        });
    }
}

