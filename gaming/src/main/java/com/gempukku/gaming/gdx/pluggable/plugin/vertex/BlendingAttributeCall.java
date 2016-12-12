package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

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

        vertexShaderBuilder.addUniformVariable("u_opacity", "float",
                new BaseShader.LocalSetter() {
                    @Override
                    public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                        BlendingAttribute blendingAttribute = (BlendingAttribute) combinedAttributes.get(BlendingAttribute.Type);
                        shader.context.setBlending(true, blendingAttribute.sourceFunction, blendingAttribute.destFunction);
                        shader.set(inputID, blendingAttribute.opacity);
                    }
                });
        vertexShaderBuilder.addVaryingVariable("v_opacity", "float");

        if (hasAlphaTest) {
            vertexShaderBuilder.addUniformVariable("u_alphaTest", "float",
                    new BaseShader.LocalSetter() {
                        @Override
                        public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                            FloatAttribute alphaTestAttribute = (FloatAttribute) combinedAttributes.get(FloatAttribute.AlphaTest);
                            shader.set(inputID, alphaTestAttribute.value);
                        }
                    });
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
