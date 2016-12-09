package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class BlendingAttributeWithoutAlphaTestCall implements PluggableVertexFunctionCall {
    @Override
    public String getFunctionName() {
        return "setBlendingVariable";
    }

    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("blendingAttribute:");
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
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
        vertexShaderBuilder.addFunction("setBlendingVariable",
                "void setBlendingVariable() {\n" +
                        "  v_opacity = u_opacity;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(BlendingAttribute.Type) && !renderable.material.has(FloatAttribute.AlphaTest);
    }
}
