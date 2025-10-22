package timmychips.relignitemodeldefinitions.property.resolver.selectcase;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import timmychips.relignitemodeldefinitions.property.handler.SelectPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.SelectDefinition;

// Returns Block State String specified
public class BlockStateCase implements SelectPropertyHandler {
    @Override
    public String getValue(ItemStack stack, LivingEntity entity, ModelTransformationMode mode, SelectDefinition.Definition definition) {
        String block_state_property = definition.blockStateProperty(); // Get specified block state from items model definition

        BlockStateComponent block_state = stack.get(DataComponentTypes.BLOCK_STATE);
        if (block_state == null) return null;
        return block_state.properties().get(block_state_property); // retrieves value from string
    }
}
