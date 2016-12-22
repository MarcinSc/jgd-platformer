package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class AttributePositionSource implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature attributePosition = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(attributePosition);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getPositionAttribute";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_position", "vec3");
        vertexShaderBuilder.addFunction("getPositionAttribute", "vec4 getPositionAttribute() {\n  return vec4(a_position, 1.0);\n}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
