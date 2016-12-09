package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;

public class DiffuseTextureTransform implements PluggableFragmentFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("diffuseTextureTransform:");
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getTransformedDiffuseTexture";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addUniformVariable("u_diffuseTexture", "sampler2D", DefaultShader.Setters.diffuseTexture);
        fragmentShaderBuilder.addVaryingVariable("v_diffuseUV", "vec2");
        fragmentShaderBuilder.addFunction("getTransformedDiffuseTexture",
                "vec4 getTransformedDiffuseTexture(vec4 color) {\n" +
                        "  return color * texture2D(u_diffuseTexture, v_diffuseUV);\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Diffuse) &&
                (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.TextureCoordinates) > 0;
    }
}
