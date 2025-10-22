package timmychips.relignitemodeldefinitions.property.type.codec;

public record ItemModelRootDefinition(
        ItemModelDefinition model,
        boolean handAnimationSwap,
        boolean oversizedInGui,
        float swapAnimationScale
) {
    public static final ItemModelRootDefinition DEFAULT = new ItemModelRootDefinition(null, true, false, 1F);
}
