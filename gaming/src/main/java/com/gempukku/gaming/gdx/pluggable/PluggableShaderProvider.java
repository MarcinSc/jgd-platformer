package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class PluggableShaderProvider implements ShaderProvider {
    private PluggableShaderBuilder pluggableShaderBuilder;
    private BitSetPluggableShaderFeatures tempFeatures = new BitSetPluggableShaderFeatures();
    private Map<BitSet, PluggableShader> shaderMap = new HashMap();

    public PluggableShaderProvider(PluggableShaderBuilder pluggableShaderBuilder) {
        this.pluggableShaderBuilder = pluggableShaderBuilder;
    }

    public Shader getShader(Renderable renderable) {
        tempFeatures.clear();

        pluggableShaderBuilder.getShaderFeatures(renderable, tempFeatures);

        BitSet featureSet = tempFeatures.getFeatureSet();

        PluggableShader pluggableShader = shaderMap.get(featureSet);
        if (pluggableShader == null) {
            pluggableShader = pluggableShaderBuilder.buildShader(renderable);
            pluggableShader.init();
            shaderMap.put((BitSet) featureSet.clone(), pluggableShader);
        }

        return pluggableShader;
    }

    public void dispose() {
        for (PluggableShader pluggableShader : shaderMap.values()) {
            pluggableShader.dispose();
        }
        shaderMap.clear();
    }
}
