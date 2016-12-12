package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class NormalCalculateCall implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature normalCalculation = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public String getFunctionName(Renderable renderable) {
        return "calculateNormal";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(normalCalculation);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_normal", "vec3");
        vertexShaderBuilder.addUniformVariable("u_normalMatrix", "mat3", DefaultShader.Setters.normalMatrix);
        vertexShaderBuilder.addVaryingVariable("v_normal", "vec3");

        if (hasSkinning(renderable))
            vertexShaderBuilder.addFunction("calculateNormal",
                    "void calculateNormal() {\n" +
                            "  v_normal = normalize((u_worldTrans * skinning * vec4(a_normal, 0.0)).xyz);\n" +
                            "}\n");
        else
            vertexShaderBuilder.addFunction("calculateNormal",
                    "void calculateNormal() {\n" +
                            "  v_normal = normalize(u_normalMatrix * a_normal);\n" +
                            "}\n");

    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) != 0;
    }

    private boolean hasSkinning(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }
}
