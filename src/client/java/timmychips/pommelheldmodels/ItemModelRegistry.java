// In a new file: ItemModelRegistry.java
package timmychips.pommelheldmodels;

import net.minecraft.util.Identifier;
import timmychips.pommelheldmodels.property.type.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemModelRegistry {
    private static final Map<Identifier, ItemModelDefinition> definitions = new HashMap<>();
    public static Set<Identifier> INVALID_MODEL_TYPES = new HashSet<>();

    public static void put(Identifier id, ItemModelDefinition definition) {
        if (validateType(id, definition)) definitions.put(id, definition);
    }

    public static boolean validateType(Identifier id, ItemModelDefinition definition) {
        Identifier specifiedType = null;

        if (definition instanceof ModelDefinition def) {
            if (!def.type().equals(MODEL)) specifiedType = def.type();
        } else if (definition instanceof ConditionDefinition def) {
            if (!def.type().equals(CONDITION)) specifiedType = def.type();
        } else if (definition instanceof SelectDefinition.Definition def) {
            if (!def.type().equals(SELECT)) specifiedType = def.type();
        } else if (definition instanceof RangeDispatchDefinition.Definition def) {
            if (!def.type().equals(RANGE)) specifiedType = def.type();
        }

        if (specifiedType != null) {
            INVALID_MODEL_TYPES.add(id);
            ClientInitializer.LOGGER.error("Couldn't parse item '{}': Unknown item model type id: {}", id, specifiedType);
            return false;
        }
        return true;
    }

    public static ItemModelDefinition get(Identifier id) {
        return definitions.get(id);
    }

    public static boolean hasDefinition(Identifier id) {
        return definitions.containsKey(id);
    }

    public static void clear() {
        INVALID_MODEL_TYPES.clear();
        definitions.clear();
    }

    public static Set<Identifier> getAllModelDependencies() {
        Set<Identifier> dependencies = new HashSet<>();
        for (ItemModelDefinition definition : definitions.values()) {
            collectModelsFromDefinition(definition, dependencies);
        }
        return dependencies;
    }

    private static void collectModelsFromDefinition(ItemModelDefinition def, Set<Identifier> out) {
        if (def instanceof ModelDefinition model) {
            out.add(model.model());

        } else if (def instanceof SelectDefinition.Definition select) {
            for (SelectDefinition.Case c : select.cases()) {
                collectModelsFromDefinition(c.model(), out);
            }

            collectModelsFromDefinition(select.fallback(), out);
        } else if (def instanceof ConditionDefinition condition) {
            collectModelsFromDefinition(condition.on_true(), out);
            collectModelsFromDefinition(condition.on_false(), out);

        } else if (def instanceof RangeDispatchDefinition.Definition range) {
            for (RangeDispatchDefinition.ThresholdEntry entry : range.entries()) {
                collectModelsFromDefinition(entry.model(), out);
            }
            collectModelsFromDefinition(range.fallback(), out);
        }
        // Extend here for other custom model types if needed
    }

    private static final Identifier MODEL = Identifier.of("minecraft:model");
    private static final Identifier CONDITION = Identifier.of("minecraft:condition");
    private static final Identifier SELECT = Identifier.of("minecraft:select");
    private static final Identifier RANGE = Identifier.of("minecraft:range_dispatch");

    private static final Set<Identifier> VALID_TYPES = Set.of(
            Identifier.of("minecraft:model"),
            Identifier.of("minecraft:condition"),
            Identifier.of("minecraft:select"),
            Identifier.of("minecraft:range_dispatch"));

    public static boolean isValidModelType(ItemModelDefinition definition) {
        if (definition instanceof ModelDefinition || definition instanceof ConditionDefinition || definition instanceof SelectDefinition.Definition || definition instanceof RangeDispatchDefinition.Definition) {

        }

        if (definition instanceof ModelDefinition def) {
            if (!def.type().equals(MODEL)) return false; ClientInitializer.LOGGER.error("Invalid Model Type");
        }
        if (definition instanceof ConditionDefinition def) {
            if (!def.type().equals(CONDITION)) return false; ClientInitializer.LOGGER.error("Invalid Condition Type");
        }
        if (definition instanceof SelectDefinition.Definition def) {
            if (!def.type().equals(SELECT)) return false; ClientInitializer.LOGGER.error("Invalid Select Type");
        }
        if (definition instanceof RangeDispatchDefinition.Definition def) {
            if (!def.type().equals(RANGE)) return false; ClientInitializer.LOGGER.error("Invalid Range Type");
        }
        return true;
    }
}

