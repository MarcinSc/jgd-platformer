package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class DiffuseColorTransform implements PluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseColorTransform = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseColorTransform);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getTransformedDiffuseColor";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addUniformVariable("u_diffuseColor", "vec4", false, UniformSetters.diffuseColor);
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
