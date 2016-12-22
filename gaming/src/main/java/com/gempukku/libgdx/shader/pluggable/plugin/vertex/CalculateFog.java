package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class CalculateFog implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature calculateFog = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(calculateFog);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "calculateFog";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_cameraPosition", "vec4", true, UniformSetters.cameraPosition);
        vertexShaderBuilder.addVaryingVariable("v_fog", "float");

        vertexShaderBuilder.addFunction("calculateFog",
                "vec4 calculateFog(vec4 position) {" +
                        "  vec3 flen = u_cameraPosition.xyz - position.xyz;\n" +
                        "  float fog = dot(flen, flen) * u_cameraPosition.w;\n" +
                        "  v_fog = min(fog, 1.0);\n" +
                        "  return position;" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.environment != null && renderable.environment.has(ColorAttribute.Fog);
    }
}
