package timmychips.pommelheldmodels.type.condition;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class ComponentBool {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Boolean test(String predicate, @Nullable JsonElement value, ItemStack stack) {
        if (stack == null || predicate == null || value == null) return false;

        Identifier predicateId = Identifier.tryParse(predicate);
        if (predicateId == null) {
            LOGGER.warn("Invalid component predicate ID '{}'", predicate);
            return false;
        }

        ItemSubPredicate.Type<?> type = Registries.ITEM_SUB_PREDICATE_TYPE.get(predicateId);
        if (type == null) {
            LOGGER.warn("Unknown component predicate type '{}'", predicateId);
            return false;
        }

        try {
            DynamicOps<JsonElement> registryOps = RegistryOps.of(
                    JsonOps.INSTANCE,
                    Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getRegistryManager());

            Optional<? extends ItemSubPredicate> parsed = type.codec()
                    .decode(registryOps, value)
                    .result()
                    .map(Pair::getFirst);

            if (parsed.isPresent()) {
                return parsed.get().test(stack);
            } else {
                LOGGER.warn("Failed to decode predicate value for '{}': {}", predicateId, value);
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("Error parsing component predicate JSON for '{}': {}", predicateId, value, e);
            return false;
        }
    }

}
