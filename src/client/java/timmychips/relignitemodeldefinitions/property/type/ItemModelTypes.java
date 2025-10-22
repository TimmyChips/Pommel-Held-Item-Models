package timmychips.relignitemodeldefinitions.property.type;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import timmychips.relignitemodeldefinitions.ClientInitializer;
import timmychips.relignitemodeldefinitions.property.helper.DefinitionIdMapper;
import timmychips.relignitemodeldefinitions.property.type.codec.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Adds definition types to mapper which will register them all
 */
public class ItemModelTypes {
    public static final DefinitionIdMapper ID_MAPPER = new DefinitionIdMapper();
    public static final Codec<ItemModelDefinition> CODEC = Codec.lazyInitialized(() -> ID_MAPPER.getCodec(Identifier.CODEC));

    // Identifiers for item model types
    private static final Identifier MODEL =     Identifier.of("minecraft:model");
    private static final Identifier CONDITION = Identifier.of("minecraft:condition");
    private static final Identifier SELECT =    Identifier.of("minecraft:select");
    private static final Identifier RANGE =     Identifier.of("minecraft:range_dispatch");
    private static final Identifier COMPOSITE = Identifier.of("minecraft:composite");
    private static final Identifier EMPTY =     Identifier.of("minecraft:empty");

    static {
        // Place all items model types into mapper
        ID_MAPPER.put(MODEL,     ModelDefinition.CODEC);
        ID_MAPPER.put(CONDITION, ConditionDefinition.codec(CODEC));
        ID_MAPPER.put(SELECT,    SelectDefinition.Definition.codec(CODEC));
        ID_MAPPER.put(RANGE,     RangeDispatchDefinition.Definition.codec(CODEC));
        ID_MAPPER.put(COMPOSITE, CompositeModelDefinition.CODEC);
        ID_MAPPER.put(EMPTY,     EmptyModelDefinition.CODEC);
    }

    /**
     * Register the items model definitions files and prepare models for ModelLoadingPlugin
     */
    public static class Registry {
        private static final Map<Identifier, ItemModelDefinition> definitions = new HashMap<>();
        private static final Map<Identifier, ItemModelRootDefinition> rootDefinitions = new HashMap<>();
        public static Set<Identifier> INVALID_MODEL_TYPES = new HashSet<>();

        // Registers the root definition object for the item id
        public static void registerRoot(Identifier id, ItemModelRootDefinition root) {
            if (root.model() != null && validateType(id, root.model())) {
                definitions.put(id, root.model()); // Put the id and ItemModelDefinition into Map
                rootDefinitions.put(id, root); // The root
            }
        }

        // For retrieving the item's root json fields (get_animation_swap, etc.)
        public static ItemModelRootDefinition getRoot(Identifier id) {
            return rootDefinitions.get(id);
        }

        /**
         *
         * @param id The item id to check
         * @param definition The model definition to check if the type is valid
         * @return If type for item definition is valid or not
         */
        public static boolean validateType(Identifier id, ItemModelDefinition definition) {
            if (definition == null) return false;
            if (!definition.type().equals(definition.getType())) {
                INVALID_MODEL_TYPES.add(id);
                ClientInitializer.LOGGER.error("Couldn't parse item '{}': Unknown item model type id: {}", id, definition.type());
                return false;
            }
            return true;
        }

        /**
         *
         * @param id The item id
         * @return The ItemModelDefinition object associated with the item
         */
        public static ItemModelDefinition get(Identifier id) {
            return definitions.get(id);
        }

        /**
         * Clear hash maps when reloading resource packs
         */
        public static void clear() {
            INVALID_MODEL_TYPES.clear();
            definitions.clear();
            rootDefinitions.clear();
        }

        /**
         *
         * @return HashSet of Identifiers to load models for in ModelLoadingPlugin
         */
        public static Set<Identifier> getAllModelDependencies() {
            Set<Identifier> dependencies = new HashSet<>();
            ClientInitializer.LOGGER.info("Definitions: {}", definitions);
            for (ItemModelDefinition definition : definitions.values()) {
                collectModelsFromDefinition(definition, dependencies);
            }
            return dependencies;
        }

        // TODO: refactor into function or stream that is recursive to return Set of Identifiers to register models for
        private static void collectModelsFromDefinition(ItemModelDefinition def, Set<Identifier> out) {
            switch (def) {
                case ModelDefinition modelDef -> {
                    out.add(modelDef.model());
                    ClientInitializer.LOGGER.info("Collecting model for {}, result: {}", modelDef, modelDef.model());
                }
                case SelectDefinition.Definition selectDef -> {
                    for (SelectDefinition.Case<String> c : selectDef.cases()) {
                        collectModelsFromDefinition(c.model(), out);
                    }
                    collectModelsFromDefinition(selectDef.fallback(), out);
                }
                case ConditionDefinition conditionDef -> {
                    collectModelsFromDefinition(conditionDef.on_true(), out);
                    collectModelsFromDefinition(conditionDef.on_false(), out);
                }

                case RangeDispatchDefinition.Definition rangeDef -> {
                    for (RangeDispatchDefinition.ThresholdEntry entry : rangeDef.entries()) {
                        collectModelsFromDefinition(entry.model(), out);
                    }
                    collectModelsFromDefinition(rangeDef.fallback(), out);
                }

//                case CompositeModelDefinition compositeDef -> {
//                    for (ItemModelDefinition defPart : compositeDef.models()) {
//                        collectModelsFromDefinition(defPart, out);
//                    }
//                }
                default -> ClientInitializer.LOGGER.error("Unexpected model definition: {}", def);
            }
        }
    }
}
