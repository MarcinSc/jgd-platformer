package com.gempukku.libgdx.shader.pluggable;


import com.gempukku.libgdx.shader.pluggable.plugin.fragment.ApplyFog;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.ApplyPerVertexLighting;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.BlendingAttributeTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.ColorAttributeTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.DiffuseColorTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.DiffuseTextureTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.WhiteColorSource;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.DiffuseAttributeSource;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.SpecularAttributeSource;
import com.gempukku.libgdx.shader.pluggable.plugin.initializer.CullFaceInitializer;
import com.gempukku.libgdx.shader.pluggable.plugin.initializer.DepthTestInitializer;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.ApplySkinningTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.AttributePositionSource;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.BlendingAttributeCall;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.CalculateFog;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.ColorAttributeCall;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.NormalCalculateCall;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.PerVertexLightingCalculateCall;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.ProjectViewTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.SkinningCalculateCall;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.TextureCooridnateAttributesCall;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.WorldTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.AmbientCubemapDiffuseLightTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.ApplyDirectionalLights;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.ApplyPointLights;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.DarkDiffuseSource;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.DarkSpecularSource;

public class PluggableShaderUtil {
    private PluggableShaderUtil() {
    }

    public static PluggableShaderBuilder createDefaultPluggableShaderBuilder() {
        DefaultPluggableShaderBuilder defaultPluggableShaderBuilder = new DefaultPluggableShaderBuilder();

        // Initializers
        defaultPluggableShaderBuilder.addRenderInitializer(new CullFaceInitializer());
        defaultPluggableShaderBuilder.addRenderInitializer(new DepthTestInitializer());

        // Vertex shader
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new ColorAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new BlendingAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SkinningCalculateCall(12));
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new NormalCalculateCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new TextureCooridnateAttributesCall());

        defaultPluggableShaderBuilder.setPositionSource(new AttributePositionSource());
        defaultPluggableShaderBuilder.addPositionProcessor(new ApplySkinningTransform());
        defaultPluggableShaderBuilder.addPositionProcessor(new WorldTransform());
        defaultPluggableShaderBuilder.addPositionProcessor(new CalculateFog());

        // Lighting vertex calculations
        PerVertexLightingCalculateCall perVertexLightingCalculateCall = new PerVertexLightingCalculateCall();
        perVertexLightingCalculateCall.setLightDiffuseSource(new DarkDiffuseSource());
        perVertexLightingCalculateCall.setLightSpecularSource(new DarkSpecularSource());

        perVertexLightingCalculateCall.addLightWrapper(new AmbientCubemapDiffuseLightTransform(2, 5));
        perVertexLightingCalculateCall.addLightWrapper(new ApplyDirectionalLights(2));
        perVertexLightingCalculateCall.addLightWrapper(new ApplyPointLights(5));

        defaultPluggableShaderBuilder.addPositionProcessor(perVertexLightingCalculateCall);

        defaultPluggableShaderBuilder.addPositionProcessor(new ProjectViewTransform());

        // Fragment shader
        defaultPluggableShaderBuilder.setColorSource(new WhiteColorSource());
        defaultPluggableShaderBuilder.addColorProcessor(new ColorAttributeTransform());
        defaultPluggableShaderBuilder.addColorProcessor(new DiffuseColorTransform());
        defaultPluggableShaderBuilder.addColorProcessor(new DiffuseTextureTransform());

        //Lighting fragment calculations

        ApplyPerVertexLighting perVertexLightingApplyCall = new ApplyPerVertexLighting();
        perVertexLightingApplyCall.setLightDiffuseSource(new DiffuseAttributeSource());
        perVertexLightingApplyCall.setLightSpecularSource(new SpecularAttributeSource());

        defaultPluggableShaderBuilder.addColorProcessor(perVertexLightingApplyCall);

        defaultPluggableShaderBuilder.addColorProcessor(new ApplyFog());
        defaultPluggableShaderBuilder.addColorProcessor(new BlendingAttributeTransform());

        return defaultPluggableShaderBuilder;
    }

    public static void main(String[] args) {
        createDefaultPluggableShaderBuilder().buildShader(null);
    }
}
