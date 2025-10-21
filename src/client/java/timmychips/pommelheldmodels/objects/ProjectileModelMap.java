package timmychips.pommelheldmodels.objects;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Adds projectile-related entities (including flying item entities) to a hashmap with the entity's ItemStack
 */
public class ProjectileModelMap {
    public static Map<Entity, ItemStack> PROJECTILE_MAP = new WeakHashMap<>();

    public static void addEntity(Entity entity, ItemStack stack) {
        PROJECTILE_MAP.put(entity, stack);
    }

    public static void removeEntity(Entity entity) {
        PROJECTILE_MAP.remove(entity);
    }

    public static boolean entityInMap(Entity entity) {
        return PROJECTILE_MAP.containsKey(entity);
    }

    // Iterates through all values in HashMap and verifies if the current rendered ItemStack matches any stored in the map
    public static boolean isProjectileStack(ItemStack stack) {
        for (ItemStack stored : PROJECTILE_MAP.values()) {
            if (ItemStack.areEqual(stored, stack)) {
                return true;
            }
        }
        return false;
    }
}
