package timmychips.pommelheldmodels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.resolver.rangeentry.CompassFloat;
import timmychips.pommelheldmodels.resolver.rangeentry.RangePropertyRegistry;
import timmychips.pommelheldmodels.type.ItemModelDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Set;

public class ClientInitializer implements ClientModInitializer {

	public static final Logger LOGGER = LogUtils.getLogger();
	private static Collection<Identifier> ModelCollection;
	public static Collection<Identifier> modelIds;

	@Override
	public void onInitializeClient() {
		// Mod's Client Entrypoint

		// Register model item predicate
			// Any .json model file with the <"pommel:is_held": 1.0> item predicate will override and render the item
			// with the specified held model file
		HeldItemPredicate.registerHeldModelPredicate();

		// Register methods using items for the is_using predicate
		UseKeyTracker.receiveUseKeyPacket();
		UseKeyTracker.clientUseKey();
		UseKeyTracker.eventUseKeyPacket();

		ModelLoadingPlugin.register(pluginContext -> {

			ResourceManager manager = MinecraftClient.getInstance().getResourceManager();

			LOGGER.info("Pommel: Reloading Resource Manager");

			ItemModelRegistry.clear();
			RangePropertyRegistry.register("minecraft:compass", new CompassFloat());

			for (Identifier id : manager.findResources("items", path -> path.getPath().endsWith(".json")).keySet()) {
				try (InputStream stream = manager.getResource(id).get().getInputStream()) {
					JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));

					JsonObject root = json.getAsJsonObject();
					JsonElement modelElement = root.get("model");

					if (modelElement != null && modelElement.isJsonObject()) {
						ItemModelDefinition.CODEC.decode(JsonOps.INSTANCE, modelElement)
								.resultOrPartial(error -> LOGGER.warn("[Pommel] Failed to decode model definition for {}: {}", id, error))
								.ifPresent(pair -> {
									// Clean up path to match item ID (remove "items/" and ".json")
									String cleanPath = id.getPath().substring("items/".length(), id.getPath().length() - ".json".length());
									Identifier itemId = Identifier.of(id.getNamespace(), cleanPath);

									// Store in registry
									ItemModelRegistry.put(itemId, pair.getFirst());
									LOGGER.info("[Pommel] Successfully decoded item model definition for: {}", itemId);
								});
					} else {
						LOGGER.warn("[Pommel] No 'model' field found in item JSON for {}", id);
					}

				} catch (Exception e) {
					LOGGER.warn("[Pommel] Failed to parse item definition for {}", id, e);
				}
			}

			modelIds = ItemModelRegistry.getAllModelDependencies();
			modelIds.forEach(id -> LOGGER.info("[Pommel] Registering model dependency: {}", id));

			LOGGER.info("Pommel: ModelLoadingPlugin is active!");
			// Test model to load
			pluginContext.addModels(modelIds);
//			pluginContext.addModels(Identifier.ofVanilla("item/a_test"));
			pluginContext.addModels(Identifier.ofVanilla("item/a_test"));
			pluginContext.addModels(Identifier.ofVanilla("abcdeeznuts"));
		});

		// TODO (model registration)
		//  Make it register custom models specified in the items model definition .json file
		//  ( the "items" folder works as well for resource packs)

//		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
//			public Identifier getFabricId() {
//				return Identifier.of("pommel", "item_model_definitions");
//			}
//
//			public void reload(ResourceManager manager) {
//				LOGGER.info("Pommel: Reloading Resource Manager");
//
//				ItemModelRegistry.clear();
//				RangePropertyRegistry.register("minecraft:compass", new CompassFloat());
//
//				for (Identifier id : manager.findResources("items", path -> path.getPath().endsWith(".json")).keySet()) {
//					try (InputStream stream = manager.getResource(id).get().getInputStream()) {
//						JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));
//
//						JsonObject root = json.getAsJsonObject();
//						JsonElement modelElement = root.get("model");
//
//						if (modelElement != null && modelElement.isJsonObject()) {
//							ItemModelDefinition.CODEC.decode(JsonOps.INSTANCE, modelElement)
//									.resultOrPartial(error -> LOGGER.warn("[Pommel] Failed to decode model definition for {}: {}", id, error))
//									.ifPresent(pair -> {
//										// Clean up path to match item ID (remove "items/" and ".json")
//										String cleanPath = id.getPath().substring("items/".length(), id.getPath().length() - ".json".length());
//										Identifier itemId = Identifier.of(id.getNamespace(), cleanPath);
//
//										// Store in registry
//										ItemModelRegistry.put(itemId, pair.getFirst());
//										LOGGER.info("[Pommel] Successfully decoded item model definition for: {}", itemId);
//									});
//						} else {
//							LOGGER.warn("[Pommel] No 'model' field found in item JSON for {}", id);
//						}
//
//					} catch (Exception e) {
//						LOGGER.warn("[Pommel] Failed to parse item definition for {}", id, e);
//					}
//				}
//			}
//		});

	}
}