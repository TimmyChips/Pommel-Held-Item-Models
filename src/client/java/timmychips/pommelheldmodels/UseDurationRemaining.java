package timmychips.pommelheldmodels;

import com.mojang.logging.LogUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;

public class UseDurationRemaining {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static float getTicksUsed(ItemStack stack, LivingEntity user) {
        if (user != null && ItemStack.areEqual(stack, user.getActiveItem())) {
            FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);

            if (foodComponent != null) {
                LOGGER.info("Food item ticks: {}", (float) (foodComponent.getEatTicks() - user.getItemUseTimeLeft()) / foodComponent.getEatTicks());
                return (float) (foodComponent.getEatTicks() - user.getItemUseTimeLeft()) / foodComponent.getEatTicks();
            }
            else {
                int maxUseTime = stack.getMaxUseTime(user);
                LOGGER.info("Usable item ticks: {}", (float) (maxUseTime - user.getItemUseTimeLeft()) / maxUseTime);
                return (float) (maxUseTime - user.getItemUseTimeLeft()) / maxUseTime;
            }

//            var test = (stack.getMaxUseTime(user) - user.getItemUseTimeLeft()) / 32f;
//            LOGGER.info(String.valueOf(test));
//            return test;
        }
        else return 0F;
    }
}
