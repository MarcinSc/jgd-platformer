package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class CelShadingDiffuseTransform implements PerVertexLightingApplyFunctionCall {
    private int shadeCountMaximum;
    // Separate due to shade count
    private PluggableShaderFeatureRegistry.PluggableShaderFeature[] celShadingFeatures;

    public CelShadingDiffuseTransform(int shadeCountMaximum) {
        this.shadeCountMaximum = shadeCountMaximum;
        this.celShadingFeatures = new PluggableShaderFeatureRegistry.PluggableShaderFeature[shadeCountMaximum];
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyCelShading";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        int celShadeCount = getCelShadeCount(renderable);
        PluggableShaderFeatureRegistry.PluggableShaderFeature celShadingFeature = celShadingFeatures[celShadeCount - 1];
        if (celShadingFeature == null) {
            celShadingFeature = PluggableShaderFeatureRegistry.registerFeature();
            celShadingFeatures[celShadeCount - 1] = celShadingFeature;
        }
        pluggableShaderFeatures.addFeature(celShadingFeature);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        fragmentShaderBuilder.addFunction("applyCelShading",
                "Lighting applyCelShading(Lighting lighting) {\n" +
                        "  lighting.diffuse.r = floor(0.5 + lighting.diffuse.r * " + shadeCountFlt + ")/" + shadeCountFlt + ";\n" +
                        "  lighting.diffuse.g = floor(0.5 + lighting.diffuse.g * " + shadeCountFlt + ")/" + shadeCountFlt + ";\n" +
                        "  lighting.diffuse.b = floor(0.5 + lighting.diffuse.b * " + shadeCountFlt + ")/" + shadeCountFlt + ";\n" +
                        "  return lighting;\n" +
                        "}\n");
    }

    private int getCelShadeCount(Renderable renderable) {
        return Math.min(shadeCountMaximum, renderable.material.get(CelShadingAttribute.class, CelShadingAttribute.CelShading).value);
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return renderable.material.has(CelShadingAttribute.CelShading);
    }
}
