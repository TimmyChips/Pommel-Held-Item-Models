package timmychips.pommelheldmodels;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemTest {
    public static Item registerItem(Item item, String id) {
        Identifier itemId = Identifier.of("pommel", id);

        Item registeredItem = Registry.register(Registries.ITEM, itemId, item);

        return registeredItem;
    }

    public static Item TEST_ITEM = registerItem(
            new Item(new Item.Settings()),
            "test_item"
    );

    public static void initItem() {
    }
}
