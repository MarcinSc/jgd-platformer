package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.lighting.PerVertexLightingApplyFunctionCall;

import java.util.LinkedList;
import java.util.List;

public class ApplyPerVertexLighting implements PluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature applyPerVertexLighting = PluggableShaderFeatureRegistry.registerFeature();

    private PluggableFragmentFunctionCall lightDiffuseSource;
    private PluggableFragmentFunctionCall lightSpecularSource;

    private List<PerVertexLightingApplyFunctionCall> lightWrappers = new LinkedList<>();

    public void setLightDiffuseSource(PluggableFragmentFunctionCall lightDiffuseSource) {
        this.lightDiffuseSource = lightDiffuseSource;
    }

    public void setLightSpecularSource(PluggableFragmentFunctionCall lightSpecularSource) {
        this.lightSpecularSource = lightSpecularSource;
    }

    public void addLightWrapper(PerVertexLightingApplyFunctionCall lightWrapper) {
        lightWrappers.add(lightWrapper);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "applyPerVertexLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(applyPerVertexLighting);
        lightDiffuseSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        boolean hasSpecular = hasSpecularCalculation(renderable);
        if (hasSpecular)
            lightSpecularSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        lightDiffuseSource.appendFunction(renderable, fragmentShaderBuilder);
        boolean specularCalculation = hasSpecularCalculation(renderable);
        if (specularCalculation)
            lightSpecularSource.appendFunction(renderable, fragmentShaderBuilder);

        StringBuilder function = new StringBuilder();

        function.append("vec4 applyPerVertexLighting(vec4 color) {\n");
        function.append("  vec3 diffuse = " + lightDiffuseSource.getFunctionName(renderable) + "();\n");
        if (specularCalculation)
            function.append("  vec3 specular = " + lightSpecularSource.getFunctionName(renderable) + "();\n");

        for (PerVertexLightingApplyFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, specularCalculation)) {
                lightWrapper.appendFunction(renderable, fragmentShaderBuilder, specularCalculation);
                function.append("  " + lightWrapper.getFunctionName(renderable, specularCalculation) + "(diffuse, specular);\n");
            }
        }

        function.append("  color.rgb = color.rgb * diffuse;\n");
        if (specularCalculation)
            function.append("  color.rgb = color.rgb + specular;\n");
        function.append("  return color;\n" +
                "}\n");

        fragmentShaderBuilder.addFunction("applyPerVertexLighting", function.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        long vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return renderable.environment != null &&
                (hasNormal(vertexMask) || hasTangentAndBiNormal(vertexMask));
    }

    private boolean hasSpecularCalculation(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Specular) || renderable.material.has(ColorAttribute.Specular);
    }

    private boolean hasNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Normal) != 0;
    }

    private boolean hasTangentAndBiNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Tangent) != 0 && (vertexMask & VertexAttributes.Usage.BiNormal) != 0;
    }
}
