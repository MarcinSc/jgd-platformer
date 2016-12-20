package com.gempukku.gaming.gdx.pluggable.plugin.fragment.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;

public class DiffuseAttributeSource implements PluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseAttributeSource = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getDiffuseVaryingValue";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseAttributeSource);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addVaryingVariable("v_lightDiffuse", "vec3");
        fragmentShaderBuilder.addFunction("getDiffuseVaryingValue",
                "vec3 getDiffuseVaryingValue() {\n" +
                        "  return v_lightDiffuse;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
