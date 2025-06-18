// In a new file: ItemModelRegistry.java
package timmychips.pommelheldmodels;

import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class ItemModelRegistry {
    private static final Map<Identifier, ItemModelDefinitionCodec.ItemModelDefinition> definitions = new HashMap<>();

    public static void put(Identifier id, ItemModelDefinitionCodec.ItemModelDefinition definition) {
        definitions.put(id, definition);
    }

    public static ItemModelDefinitionCodec.ItemModelDefinition get(Identifier id) {
        return definitions.get(id);
    }

    public static boolean hasDefinition(Identifier id) {
        return definitions.containsKey(id);
    }

    public static void clear() {
        definitions.clear();
    }
}

