package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class SpecularAttributeSource implements PluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseAttributeSource = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getSpecularVaryingValue";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseAttributeSource);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addVaryingVariable("v_lightSpecular", "vec3");
        fragmentShaderBuilder.addFunction("getSpecularVaryingValue",
                "vec3 getSpecularVaryingValue() {\n" +
                        "  return v_lightSpecular;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
