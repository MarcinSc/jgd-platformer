package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

import java.util.LinkedList;
import java.util.List;

public class PerVertexLightingCalculateCall implements PluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseCalculation = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature specularCalculation = PluggableShaderFeatureRegistry.registerFeature();

    private PluggableVertexFunctionCall lightDiffuseSource;
    private PluggableVertexFunctionCall lightSpecularSource;

    private List<PluggableVertexFunctionCall> lightDiffuseWrappers = new LinkedList<>();
    private List<PluggableVertexFunctionCall> lightSpecularWrappers = new LinkedList<>();

    public void setLightDiffuseSource(PluggableVertexFunctionCall lightDiffuseSource) {
        this.lightDiffuseSource = lightDiffuseSource;
    }

    public void setLightSpecularSource(PluggableVertexFunctionCall lightSpecularSource) {
        this.lightSpecularSource = lightSpecularSource;
    }

    public void addLightDiffuseWrapper(PluggableVertexFunctionCall lightDiffuseWrapper) {
        lightDiffuseWrappers.add(lightDiffuseWrapper);
    }

    public void addLightSpecularWrapper(PluggableVertexFunctionCall lightSpecularWrapper) {
        lightSpecularWrappers.add(lightSpecularWrapper);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "calculatePerVertexLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseCalculation);
        appendShaderFeatures(renderable, pluggableShaderFeatures, lightDiffuseSource, lightDiffuseWrappers);

        if (hasSpecularCalculation(renderable)) {
            pluggableShaderFeatures.addFeature(specularCalculation);
            appendShaderFeatures(renderable, pluggableShaderFeatures, lightSpecularSource, lightSpecularWrappers);
        }
    }

    private void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, PluggableVertexFunctionCall source, List<PluggableVertexFunctionCall> wrappers) {
        source.appendShaderFeatures(renderable, pluggableShaderFeatures);
        for (PluggableVertexFunctionCall wrapper : wrappers) {
            if (wrapper.isProcessing(renderable))
                wrapper.appendShaderFeatures(renderable, pluggableShaderFeatures);
        }
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addVaryingVariable("v_lightDiffuse", "vec3");
        boolean specularCalculation = hasSpecularCalculation(renderable);
        if (specularCalculation)
            vertexShaderBuilder.addVaryingVariable("v_lightSpecular", "vec3");

        StringBuilder sb = new StringBuilder();
        sb.append("void calculatePerVertexLighting() {\n");
        sb.append(" v_lightDiffuse = " + createDiffuseChain(renderable, vertexShaderBuilder) + ";");
        if (specularCalculation)
            sb.append("  v_lightSpecular = " + createSpecularChain(renderable, vertexShaderBuilder) + ";");
        sb.append("}\n");

        vertexShaderBuilder.addFunction("calculatePerVertexLighting", sb.toString());
    }

    private String createDiffuseChain(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        return createChain(renderable, vertexShaderBuilder, lightDiffuseSource, lightDiffuseWrappers).toString();
    }

    private String createSpecularChain(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        return createChain(renderable, vertexShaderBuilder, lightSpecularSource, lightSpecularWrappers).toString();
    }

    private StringBuilder createChain(Renderable renderable, VertexShaderBuilder vertexShaderBuilder, PluggableVertexFunctionCall source, List<PluggableVertexFunctionCall> wrappers) {
        StringBuilder chain = new StringBuilder();
        String functionName = source.getFunctionName(renderable);
        source.appendFunction(renderable, vertexShaderBuilder);
        String executionChain = functionName + "()";

        for (PluggableVertexFunctionCall wrapper : wrappers) {
            if (wrapper.isProcessing(renderable)) {
                wrapper.appendFunction(renderable, vertexShaderBuilder);
                executionChain = wrapper.getFunctionName(renderable) + "(" + executionChain + ")";
            }
        }
        return chain;
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
