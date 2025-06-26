package timmychips.pommelheldmodels.codec.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Optional;

public class ComponentBool {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Boolean testComponentPredicate(String predicate, String value, ItemStack stack) {

        if (stack == null || predicate == null || value == null) return false;

        Identifier predicateId;
        if (predicate.contains(":")) {
            String[] parts = predicate.split(":");
            String id = parts[0];
            String path = parts[1];
            predicateId = Identifier.of(id, path);
        }
        else {
            predicateId = Identifier.ofVanilla("predicate");
        }

        ItemSubPredicate.Type<?> type = Registries.ITEM_SUB_PREDICATE_TYPE.get(predicateId);

        if (type == null) {
            LOGGER.warn("Unknown item component predicate: '{}'", predicateId);
            return false;
        }

        // Parse the "value" string as JSON
        JsonElement element = JsonParser.parseString(value);


        // Decode the ItemSubPredicate instance from the component's Codec
        Optional<? extends ItemSubPredicate> maybePredicate = type.codec().decode(JsonOps.INSTANCE, element)
                .result()
                .map(Pair::getFirst);

        if (maybePredicate.isPresent()) {
            return maybePredicate.get().test(stack);
        } else {
            LOGGER.warn("Failed to decode component predicate for '{}': {}", predicateId, value);
            return false;
        }
    }

}
