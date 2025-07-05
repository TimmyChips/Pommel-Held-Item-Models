package timmychips.pommelheldmodels.resolver;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import timmychips.pommelheldmodels.resolver.condition.*;
import timmychips.pommelheldmodels.helper.MouseHelper;

public class ConditionValueResolver {

    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean evaluate(
            Identifier property,
            @Nullable String predicate, @Nullable JsonElement value,
            @Nullable String component, @Nullable Boolean ignore_default,
            KeyBinding keybind,
            ItemStack stack, LivingEntity entity) {

//        property = StringIDHelper.parseStringtoID(property, stack); // formats string with vanilla namespace (turns "broken" to "minecraft:broken")
        String propertyStr = property.toString();

        return switch (propertyStr) {
            // Vanilla Condition properties
            case "minecraft:broken" -> stack.getMaxDamage() - stack.getDamage() <= 1;
            case "minecraft:carried" -> CarriedBool.test(entity, stack);
            case "minecraft:component" -> ComponentBool.test(predicate, value, stack);
            case "minecraft:damaged" -> stack.isDamaged();
            case "minecraft:extended_view" -> Screen.hasShiftDown();
            case "minecraft:fishing_rod/cast" -> FishingRodCastBool.test(entity);
            case "minecraft:has_component" -> HasComponentBool.testHasComponent(component, ignore_default, stack);
            case "minecraft:keybind_down" -> KeybindDownBool.testKeybind(keybind);
            case "minecraft:selected" -> SelectedBool.test(entity, stack);
            case "minecraft:using_item" -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack;
            case "minecraft:view_entity" -> ViewEntityBool.test(entity);
            case "minecraft:custom_model_data" -> CustomModelDataBool.test(stack); // Only true if item has custom_model_data component

            // Modded Condition properties
            case "pommel:hovered_item" -> MouseHelper.isHoveredOverStack(stack, MinecraftClient.getInstance());

            // Extend with more custom logic here
            default -> false;
        };
    }
}
