package com.gempukku.gaming.gdx.pluggable;


import com.gempukku.gaming.gdx.pluggable.plugin.fragment.BlendingAttributeWithAlphaTestTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.BlendingAttributeWithoutAlphaTestTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.ColorAttributeTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.DiffuseColorTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.DiffuseTextureTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.fragment.WhiteColorSource;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.ApplySkinningTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.AttributePositionSource;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.BlendingAttributeWithAlphaTestCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.BlendingAttributeWithoutAlphaTestCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.CalculateFog;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.ColorAttributeCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.DiffuseTextureAttributeCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.ProjectViewTransform;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.SkinningCalculateCall;
import com.gempukku.gaming.gdx.pluggable.plugin.vertex.WorldTransform;

public class PluggableShaderUtil {
    private PluggableShaderUtil() {
    }

    public static PluggableShaderBuilder createDefaultPluggableShaderBuilder() {
        DefaultPluggableShaderBuilder defaultPluggableShaderBuilder = new DefaultPluggableShaderBuilder();

        defaultPluggableShaderBuilder.setPositionSource(new AttributePositionSource());
        defaultPluggableShaderBuilder.addPositionWrapper(new ApplySkinningTransform());
        defaultPluggableShaderBuilder.addPositionWrapper(new WorldTransform());
        defaultPluggableShaderBuilder.addPositionWrapper(new ProjectViewTransform());
        defaultPluggableShaderBuilder.addPositionWrapper(new CalculateFog());

        defaultPluggableShaderBuilder.addAdditionalVertexCall(new ColorAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new BlendingAttributeWithAlphaTestCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new BlendingAttributeWithoutAlphaTestCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SkinningCalculateCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new DiffuseTextureAttributeCall());

        defaultPluggableShaderBuilder.setColorSource(new WhiteColorSource());
        defaultPluggableShaderBuilder.addColorWrapper(new ColorAttributeTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new DiffuseColorTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new DiffuseTextureTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new BlendingAttributeWithAlphaTestTransform());
        defaultPluggableShaderBuilder.addColorWrapper(new BlendingAttributeWithoutAlphaTestTransform());

        return defaultPluggableShaderBuilder;
    }

    public static void main(String[] args) {
        createDefaultPluggableShaderBuilder().buildShader(null);
    }
}
