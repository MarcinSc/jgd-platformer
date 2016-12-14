package com.gempukku.gaming.gdx.pluggable;


import com.gempukku.gaming.gdx.pluggable.plugin.fragment.BlendingAttributeTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.ColorAttributeTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.DiffuseColorTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.DiffuseTextureTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.WhiteColorSource;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.ApplySkinningTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.AttributePositionSource;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.BlendingAttributeCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.CalculateFog;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.ColorAttributeCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.DarkDiffuseSource;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.DarkSpecularSource;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.NormalCalculateCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.PerVertexLightingCalculateCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.ProjectViewTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.SkinningCalculateCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.TextureCooridnateAttributesCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.WorldTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting.AmbientCubemapDiffuseLightTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting.ApplyDirectionalLights;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting.ApplyPointLights;

public class PluggableShaderUtil {
    private PluggableShaderUtil() {
    }

    public static PluggableShaderBuilder createDefaultPluggableShaderBuilder() {
        DefaultPluggableShaderBuilder defaultPluggableShaderBuilder = new DefaultPluggableShaderBuilder();

        // Vertex shader
        defaultPluggableShaderBuilder.setPositionSource(new AttributePositionSource());
        defaultPluggableShaderBuilder.addPositionWrapper(new ApplySkinningTransform());
        defaultPluggableShaderBuilder.addPositionWrapper(new WorldTransform());
        defaultPluggableShaderBuilder.addPositionWrapper(new CalculateFog());

        // Lighting vertex calculations
        PerVertexLightingCalculateCall perVertexLightingCalculateCall = new PerVertexLightingCalculateCall();
        perVertexLightingCalculateCall.setLightDiffuseSource(new DarkDiffuseSource());
        perVertexLightingCalculateCall.setLightSpecularSource(new DarkSpecularSource());

        perVertexLightingCalculateCall.addLightWrapper(new AmbientCubemapDiffuseLightTransform(2, 5));
        perVertexLightingCalculateCall.addLightWrapper(new ApplyDirectionalLights(2));
        perVertexLightingCalculateCall.addLightWrapper(new ApplyPointLights(5));

        defaultPluggableShaderBuilder.addPositionWrapper(perVertexLightingCalculateCall);

        defaultPluggableShaderBuilder.addPositionWrapper(new ProjectViewTransform());

        defaultPluggableShaderBuilder.addAdditionalVertexCall(new ColorAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new BlendingAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SkinningCalculateCall(12));
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new NormalCalculateCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new TextureCooridnateAttributesCall());

        // Fragment shader
        defaultPluggableShaderBuilder.setColorSource(new WhiteColorSource());
        defaultPluggableShaderBuilder.addColorWrapper(new ColorAttributeTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new DiffuseColorTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new DiffuseTextureTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new BlendingAttributeTransform());

        return defaultPluggableShaderBuilder;
    }

    public static void main(String[] args) {
        createDefaultPluggableShaderBuilder().buildShader(null);
    }
}
