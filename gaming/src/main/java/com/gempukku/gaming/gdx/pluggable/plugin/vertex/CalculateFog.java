package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class CalculateFog implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        if (isProcessing(renderable))
            stringBuilder.append("calculateFog:");
    }

    @Override
    public String getFunctionName() {
        return "applyFog";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_cameraPosition", "vec4", DefaultShader.Setters.cameraPosition);
        vertexShaderBuilder.addVaryingVariable("v_fog", "float");

        vertexShaderBuilder.addFunction("applyFog",
                "vec4 applyFog(vec4 position) {" +
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
