package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class TextureCooridnateAttributesCall implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        if (isProcessingDiffuse(renderable))
            stringBuilder.append("diffuseTextureAttribute:");
        if (isProcessingSpecular(renderable))
            stringBuilder.append("specularTextureAttribute:");
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "setTextureCoordinates";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_texCoord0", "vec2");
        boolean processingDiffuse = isProcessingDiffuse(renderable);
        boolean processingSpecular = isProcessingSpecular(renderable);
        if (processingDiffuse) {
            vertexShaderBuilder.addUniformVariable("u_diffuseUVTransform", "vec4", DefaultShader.Setters.diffuseUVTransform);
            vertexShaderBuilder.addVaryingVariable("v_diffuseUV", "vec2");
        }
        if (processingSpecular) {
            vertexShaderBuilder.addUniformVariable("u_specularUVTransform", "vec4", DefaultShader.Setters.specularUVTransform);
            vertexShaderBuilder.addVaryingVariable("v_specularUV", "vec2");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("void setTextureCoordinates() {\n");
        if (processingDiffuse)
            sb.append("  v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;\n");
        if (processingSpecular)
            sb.append("  v_specularUV = u_specularUVTransform.xy + a_texCoord0 * u_specularUVTransform.zw;\n");
        sb.append("}\n");

        vertexShaderBuilder.addFunction("setTextureCoordinates", sb.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.TextureCoordinates) > 0
                && (isProcessingDiffuse(renderable) || isProcessingSpecular(renderable));
    }

    private boolean isProcessingDiffuse(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Diffuse);
    }

    private boolean isProcessingSpecular(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Specular);
    }
}
