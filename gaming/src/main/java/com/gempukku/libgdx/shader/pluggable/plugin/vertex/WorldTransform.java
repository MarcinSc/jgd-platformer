package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class WorldTransform implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature worldTransform = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(worldTransform);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "transformToWorld";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_worldTrans", "mat4", false, UniformSetters.worldTrans);
        vertexShaderBuilder.addFunction("transformToWorld",
                "vec4 transformToWorld(vec4 position) {\n" +
                        "  return u_worldTrans * position;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
