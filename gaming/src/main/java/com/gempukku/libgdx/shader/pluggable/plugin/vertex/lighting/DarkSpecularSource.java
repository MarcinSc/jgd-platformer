package com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class DarkSpecularSource implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature darkSpecular = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(darkSpecular);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getDarkSpecular";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addFunction("getDarkSpecular",
                "vec3 getDarkSpecular(vec4 position) {\n" +
                        "  return vec3(0.0);\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
