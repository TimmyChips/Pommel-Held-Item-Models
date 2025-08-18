package timmychips.pommelheldmodels;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;

public class PredicateRenderModeMap {
    String namespace;
    public static HashMap<Identifier, List<ModelTransformationMode>> PREDICATE_RENDER_MODE_MAP = new HashMap<>();

    public PredicateRenderModeMap(String identifierNamespace) {
        this.namespace = identifierNamespace;
    }

    public void addToMap(String predicate, List<ModelTransformationMode> renderModeList) {
        Identifier predicateId = getId(predicate);
        PREDICATE_RENDER_MODE_MAP.put(predicateId, renderModeList);
    }

    public void addToMap(String predicate, ModelTransformationMode renderMode) {
        Identifier predicateId = getId(predicate);
        PREDICATE_RENDER_MODE_MAP.put(predicateId, List.of(renderMode));
    }

    private Identifier getId(String predicate) {
        return Identifier.of(this.namespace, predicate);
    }
}
