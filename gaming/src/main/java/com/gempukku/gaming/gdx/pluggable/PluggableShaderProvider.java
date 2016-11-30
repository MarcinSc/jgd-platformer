package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

import java.util.HashMap;
import java.util.Map;

public class PluggableShaderProvider implements ShaderProvider {
    private PluggableShaderBuilder pluggableShaderBuilder;
    private Map<String, PluggableShader> shaderMap = new HashMap();

    public PluggableShaderProvider(PluggableShaderBuilder pluggableShaderBuilder) {
        this.pluggableShaderBuilder = pluggableShaderBuilder;
    }

    public Shader getShader(Renderable renderable) {
        String shaderIdentifier = pluggableShaderBuilder.getShaderIdentifier(renderable);
        PluggableShader pluggableShader = shaderMap.get(shaderIdentifier);
        if (pluggableShader == null) {
            pluggableShader = pluggableShaderBuilder.buildShader(renderable);
            pluggableShader.init();
            shaderMap.put(shaderIdentifier, pluggableShader);
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
