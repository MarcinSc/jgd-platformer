package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting.PerVertexLightingFunctionCall;

import java.util.LinkedList;
import java.util.List;

public class PerVertexLightingCalculateCall implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseCalculation = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature specularCalculation = PluggableShaderFeatureRegistry.registerFeature();

    private PluggableVertexFunctionCall lightDiffuseSource;
    private PluggableVertexFunctionCall lightSpecularSource;

    private List<PerVertexLightingFunctionCall> lightWrappers = new LinkedList<>();

    public void setLightDiffuseSource(PluggableVertexFunctionCall lightDiffuseSource) {
        this.lightDiffuseSource = lightDiffuseSource;
    }

    public void setLightSpecularSource(PluggableVertexFunctionCall lightSpecularSource) {
        this.lightSpecularSource = lightSpecularSource;
    }

    public void addLightWrapper(PerVertexLightingFunctionCall lightWrapper) {
        lightWrappers.add(lightWrapper);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "calculatePerVertexLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseCalculation);
        boolean hasSpecular = hasSpecularCalculation(renderable);
        lightDiffuseSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        if (hasSpecular)
            lightSpecularSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        for (PerVertexLightingFunctionCall lightWrapper : lightWrappers) {
            lightWrapper.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
        }
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addVaryingVariable("v_lightDiffuse", "vec3");
        boolean specularCalculation = hasSpecularCalculation(renderable);
        if (specularCalculation)
            vertexShaderBuilder.addVaryingVariable("v_lightSpecular", "vec3");

        lightDiffuseSource.appendFunction(renderable, vertexShaderBuilder);
        if (specularCalculation)
            lightSpecularSource.appendFunction(renderable, vertexShaderBuilder);

        StringBuilder sb = new StringBuilder();
        sb.append("vec4 calculatePerVertexLighting(vec4 position) {\n");
        sb.append("  vec3 lightDiffuse = " + lightDiffuseSource.getFunctionName(renderable) + "(position);\n");
        if (specularCalculation)
            sb.append("  vec3 lightSpecular = " + lightSpecularSource.getFunctionName(renderable) + "(position);\n");
        else
            sb.append("  vec3 lightSpecular = vec3(0.0);\n");

        for (PerVertexLightingFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, specularCalculation)) {
                lightWrapper.appendFunction(renderable, vertexShaderBuilder, specularCalculation);
                sb.append("  " + lightWrapper.getFunctionName(renderable, specularCalculation) + "(position, lightDiffuse, lightSpecular);\n");
            }
        }

        sb.append("  v_lightDiffuse = lightDiffuse;\n");
        if (specularCalculation)
            sb.append("  v_lightSpecular = lightSpecular;\n");
        sb.append("  return position;\n");
        sb.append("}\n");

        vertexShaderBuilder.addFunction("calculatePerVertexLighting", sb.toString());
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

    private boolean hasFogCalculation(Renderable renderable) {
        return renderable.environment.has(ColorAttribute.Fog);
    }

    private boolean hasNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Normal) != 0;
    }

    private boolean hasTangentAndBiNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Tangent) != 0 && (vertexMask & VertexAttributes.Usage.BiNormal) != 0;
    }
}
