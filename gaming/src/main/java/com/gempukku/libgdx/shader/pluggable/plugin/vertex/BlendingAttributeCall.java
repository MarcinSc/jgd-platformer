package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class BlendingAttributeCall implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature blendingTransform = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature blendingTransformWithAlphaTest = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        if (hasAlphaTest(renderable))
            pluggableShaderFeatures.addFeature(blendingTransformWithAlphaTest);
        else
            pluggableShaderFeatures.addFeature(blendingTransform);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "setBlendingVariable";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        boolean hasAlphaTest = hasAlphaTest(renderable);

        vertexShaderBuilder.addUniformVariable("u_opacity", "float", false, UniformSetters.blending);
        vertexShaderBuilder.addVaryingVariable("v_opacity", "float");

        if (hasAlphaTest) {
            vertexShaderBuilder.addUniformVariable("u_alphaTest", "float", false, UniformSetters.alphaTest);
            vertexShaderBuilder.addVaryingVariable("v_alphaTest", "float");
        }

        if (hasAlphaTest) {
            vertexShaderBuilder.addFunction("setBlendingVariable",
                    "void setBlendingVariable() {\n" +
                            "  v_opacity = u_opacity;\n" +
                            "  v_alphaTest = u_alphaTest;\n" +
                            "}\n");
        } else {
            vertexShaderBuilder.addFunction("setBlendingVariable",
                    "void setBlendingVariable() {\n" +
                            "  v_opacity = u_opacity;\n" +
                            "}\n");
        }
    }

    private boolean hasAlphaTest(Renderable renderable) {
        return renderable.material.has(FloatAttribute.AlphaTest);
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(BlendingAttribute.Type);
    }
}
