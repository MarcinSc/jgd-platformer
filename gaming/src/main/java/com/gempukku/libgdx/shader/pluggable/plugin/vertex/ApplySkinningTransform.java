package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class ApplySkinningTransform implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature applySkinning = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(applySkinning);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "transformWithSkinning";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addFunction("transformWithSkinning",
                "vec4 transformWithSkinning(vec4 position) {\n" +
                        "  return skinning * position;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }
}
