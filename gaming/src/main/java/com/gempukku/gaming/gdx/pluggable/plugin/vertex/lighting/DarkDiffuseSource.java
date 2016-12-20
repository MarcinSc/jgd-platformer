package com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class DarkDiffuseSource implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature darkDiffuse = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(darkDiffuse);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getDarkDiffuse";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addFunction("getDarkDiffuse",
                "vec3 getDarkDiffuse(vec4 position) {\n" +
                        "  return vec3(0.0);\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
