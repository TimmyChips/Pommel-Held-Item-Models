package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class HeldModelManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final HashMap<String, Identifier> HELD_MODELS = new HashMap<>();

    public record ModelData(HashMap<String, Identifier> modelMap, List<Identifier> modelList) {}

    public static void init() {

        // Asynchronously registers models from the Hash Map as it computes them
            // In our case, we're running the prepareData method, and we "promise" to return the modelData record class results in the future
            // modelData record results (List, HashMap) are used to register the models (from the list), and get append to HELD_MODELS HashMap
            // By getting the results after the promise is fulfiled, we can get then register the models with PreparableModelLoadingPlugin and add to our HashMap
            // Literal black magic
        PreparableModelLoadingPlugin.register(HeldModelManager::prepareData, (modelData, pluginContext) -> {
            pluginContext.addModels(modelData.modelList()); // Registers all the held model values from List
            HELD_MODELS.putAll(modelData.modelMap()); // Adds Item (Key) and Held Model Path (Value) to HashMap
        });
    }

    // Gets a List of held item models for registering them, as well as a HashMap with the item id (Key) and held item model (Value) for the rest of the mod. Path is gotten from the held models in <minecraft:models/pommel_held_models/>
    // Asynchronous, non-blocking code magic
    private static CompletableFuture<ModelData> prepareData(ResourceManager manager, Executor executor) {

        // Returns and supplies asynchronous Interface with our new HashMap
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Registering Held Item Models!");

            HELD_MODELS.clear(); // Clears Hash Map to fetch models from scratch
                                      // (Not clearing the list will cause models that were deleted to still be present when reloading resource packs)

            String model_path = "models/pommel_held_models"; // Starting model path (final path will be minecraft:models/pommel_held_models)
            Map<Identifier, Resource> map = manager.findResources(model_path, path -> { // Gets a map of the model resources
                return path.toString().endsWith(".json"); // Found the model resources
            });

            HashMap<String, Identifier> modelMap = new HashMap<>();
            List<Identifier> modelList = new ArrayList<>();

            Iterator<Identifier> iter = map.keySet().iterator(); // Creates variable to iterate through for every Key (model file) in the resources=
            while(iter.hasNext()) { // Continues if there's a model file Key
                Identifier resource = iter.next(); // Resource = current Map Key

                // Gets strings for creating the HELD_MODELS HashMap
                String[] split = resource.toString().split(":" + model_path + "/"); // Splits the resource into 2 parts in a String Array[]
                // "minecraft:pommel_held_models/stick" will be split into -> "minecraft" and "stick"
                String id = split[0]; // Gets the id (e.g. "minecraft")
                String item = split[1].split(".json")[0]; // Gets the item before extension (e.g. stick)

                String itemid = id + ":" + item; // Combines id and item into one string
                Identifier modelIdentifier = Identifier.of(id, "pommel_held_models/" + item);

                // LOGGER.info("Registered held item model: " + itemid); // For debug

                modelList.add(modelIdentifier); // Adds Identifier of Held Model to list for registering models
                modelMap.put(itemid, modelIdentifier); // Adds to HashMap for use elsewhere in the mod
                                                       // HashMap.put(Key, Value)
                                                       // HashMap.put(minecraft:stick [String Key], minecraft:pommel_held_models/stick [Identifier Value])
                                                       // Final output will be {minecraft:stick = minecraft:pommel_held_models/stick, [...]}

            }
            return new ModelData(modelMap, modelList); // Returns record class with the List and HashMap
        }, executor);
    }

    // Returns true if the item has a KEY in the HashMap
        // E.g. Item "minecraft:diamond_pickaxe" is in "minecraft:diamond_pickaxe" (Key) = "minecraft:pommel_held_models/diamond_pickaxe" (Value)
        // returns True since item is key in HashMap
    public static boolean hasHeldModel(String item) {
        return HELD_MODELS.containsKey(item);
    }

    // Gets the VALUE of the key in the HashMap
        // E.g. Item "minecraft:golden_hoe" key in HashMap has value "minecraft:pommel_held_models/golden_hoe"
    public static Identifier getHeldModel(String item) {
        return HELD_MODELS.get(item); // Returns held model Identifier value of key
    }
}
