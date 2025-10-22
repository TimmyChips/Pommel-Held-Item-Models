package timmychips.relignitemodeldefinitions.property.resolver.condition.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import timmychips.relignitemodeldefinitions.property.handler.ConditionPropertyHandler;
import timmychips.relignitemodeldefinitions.property.type.codec.ConditionDefinition;

// Returns if player is in matching "fluid" field in items model definition
// "fluid" json field - optional; is id minecraft:water by default
public class SubmergedBool implements ConditionPropertyHandler {
    @Override
    public boolean getValue(ItemStack stack, LivingEntity entity, ConditionDefinition definition) {
        Identifier fluid = definition.submergedFluid(); // is minecraft:water by default
        return submergedInFluidCheck(entity, fluid);
    }

    private static boolean submergedInFluidCheck(LivingEntity entity, Identifier fluidIdToMatch) {
        Vec3d eyePos = entity.getEyePos();
        BlockPos fluidBlock = BlockPos.ofFloored(eyePos);
        FluidState fluidState = entity.getWorld().getFluidState(fluidBlock); // Get fluidState entity is submerged in

        if (!fluidState.isEmpty()) {
            Fluid fluid = fluidState.getFluid();
            Identifier fluidId = Registries.FLUID.getId(fluid); // Get id of fluid
            return fluidId.equals(fluidIdToMatch); // Matches specified fluid from json file
        }
        return false;
    }
}
