package timmychips.pommelheldmodels.helper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import timmychips.pommelheldmodels.mixin.client.HandleSlotAccessor;

public class MouseHelper {
    public static boolean isHoveredOverStack(ItemStack target, MinecraftClient client) {

        if (!(client.currentScreen instanceof HandledScreen<?> screen)) return false;

        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();


        int guiLeft = ((HandleSlotAccessor) screen).getX(); // Get screen coordinates from Mixin accessor
        int guiTop = ((HandleSlotAccessor) screen).getY();

        for (Slot slot : screen.getScreenHandler().slots) {
            int slotX = guiLeft + slot.x;
            int slotY = guiTop + slot.y;

            boolean mouseOver = mouseX >= slotX - 1 && mouseX < slotX + 17 && // numbers are offsets so it highlights correctly
                    mouseY >= slotY - 1 && mouseY < slotY + 17;

            if (mouseOver && slot.getStack() == target) { // returns true if rendered model matches the hovered item
                return true;
            }
        }

        return false;
    }
}
