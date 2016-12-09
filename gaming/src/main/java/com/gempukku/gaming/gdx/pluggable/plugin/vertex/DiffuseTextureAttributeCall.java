package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class DiffuseTextureAttributeCall implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("diffuseTextureAttribute:");
    }

    @Override
    public String getFunctionName() {
        return "setDiffuseTextureCoordinates";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_texCoord0", "vec2");
        vertexShaderBuilder.addUniformVariable("u_diffuseUVTransform", "vec4", DefaultShader.Setters.diffuseUVTransform);
        vertexShaderBuilder.addVaryingVariable("v_diffuseUV", "vec2");

        vertexShaderBuilder.addFunction("setDiffuseTextureCoordinates",
                "void setDiffuseTextureCoordinates() {\n" +
                        "  v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Diffuse) &&
                (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.TextureCoordinates) > 0;
    }
}
