package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;

public class DiffuseColorTransform implements PluggableFragmentFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("diffuseColorTransform:");
    }

    @Override
    public String getFunctionName() {
        return "getTransformedDiffuseColor";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addUniformVariable("u_diffuseColor", "vec4", DefaultShader.Setters.diffuseColor);
        fragmentShaderBuilder.addFunction("getTransformedDiffuseColor",
                "vec4 getTransformedDiffuseColor(vec4 color) {\n" +
                        "  return color * u_diffuseColor;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(ColorAttribute.Diffuse);
    }
}
