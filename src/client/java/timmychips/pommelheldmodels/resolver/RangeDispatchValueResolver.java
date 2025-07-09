package timmychips.pommelheldmodels.resolver;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.resolver.rangeentry.UseDurationFloat;

public class RangeDispatchValueResolver {
    public static float evaluate(
            Identifier property, Float scale,
            @Nullable String target,
            ItemStack stack, LivingEntity entity) {

        String propertyStr = property.toString();

        return switch (propertyStr) {
            case "minecraft:use_duration" -> UseDurationFloat.test(entity, stack, scale);
            case "minecraft:bundle/fullness" -> BundleItem.getAmountFilled(stack) * scale;
            case "minecraft:compass" ->
            //case "minecraft:compass" ->

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
