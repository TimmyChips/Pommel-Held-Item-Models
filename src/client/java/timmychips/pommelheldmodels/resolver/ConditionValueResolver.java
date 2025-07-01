package timmychips.pommelheldmodels.resolver;

import com.google.gson.JsonElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import timmychips.pommelheldmodels.type.condition.ComponentBool;
import timmychips.pommelheldmodels.type.condition.HasComponentBool;
import timmychips.pommelheldmodels.type.condition.KeybindDownBool;
import timmychips.pommelheldmodels.helper.MouseHelper;
import timmychips.pommelheldmodels.type.condition.CarriedBool;
import timmychips.pommelheldmodels.type.condition.FishingRodCastBool;
import timmychips.pommelheldmodels.type.condition.SelectedBool;

public class ConditionValueResolver {

    public static boolean evaluate(
            Identifier property,
            @Nullable String predicate, @Nullable JsonElement value,
            @Nullable String component, @Nullable Boolean ignore_default,
            KeyBinding keybind,
            ItemStack stack, LivingEntity entity) {

//        property = StringIDHelper.parseStringtoID(property, stack); // formats string with vanilla namespace (turns "broken" to "minecraft:broken")
        String propertyStr = property.toString();

        return switch (propertyStr) {
            case "minecraft:broken" -> stack.getMaxDamage() - stack.getDamage() <= 1;
            case "minecraft:carried" -> CarriedBool.test(entity, stack);
            case "minecraft:component" -> ComponentBool.test(predicate, value, stack);
            case "minecraft:damaged" -> stack.isDamaged();
            case "minecraft:extended_view" -> Screen.hasShiftDown();
            case "minecraft:fishing_rod/cast" -> FishingRodCastBool.test(entity);
            case "minecraft:has_component" -> HasComponentBool.testHasComponent(component, ignore_default, stack);
            case "pommel:hovered_item" -> MouseHelper.isHoveredOverStack(stack, MinecraftClient.getInstance());
            case "minecraft:keybind_down" -> KeybindDownBool.testKeybind(keybind);
            case "minecraft:selected" -> SelectedBool.test(entity, stack);
            case "minecraft:using_item" -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack;

            // Extend with more custom logic here
            default -> false;
        };
    }
}
