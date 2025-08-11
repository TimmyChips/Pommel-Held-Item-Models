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

    public static void put(Identifier id, ItemModelDefinition definition) {
        definitions.put(id, definition);
    }

    public static ItemModelDefinition get(Identifier id) {
        return definitions.get(id);
    }

    public static boolean hasDefinition(Identifier id) {
        return definitions.containsKey(id);
    }

    public static void clear() {
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
}

