package timmychips.pommelheldmodels.resolver;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class RangeDispatchValueResolver {
    public static float evaluate(Identifier property, Float scale, ItemStack stack, LivingEntity entity) {

        String propertyStr = property.toString();

        return switch (propertyStr) {
            case "minecraft:use_duration" -> {
                if (entity != null) {
                    yield entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) * scale;
                }
                yield 0f;
            }

            case "minecraft:bundle/fullness" -> BundleItem.getAmountFilled(stack);

//            case "minecraft:compass" -> {
//                World world = entity.getWorld();
//                LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
//                yield lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse((Object)null) : CompassItem.createSpawnPos(world);
//            }

            // Add more ranged float-returning properties here
            default -> 0f;
        };
    }
}
