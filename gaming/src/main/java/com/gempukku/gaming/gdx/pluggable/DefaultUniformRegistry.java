package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

import java.util.HashMap;
import java.util.Map;

public class DefaultUniformRegistry implements UniformRegistry {
    private Map<String, BaseShader.Setter> uniforms = new HashMap<String, BaseShader.Setter>();

    @Override
    public void registerUniform(String name, BaseShader.Setter setter) {
        if (uniforms.containsKey(name))
            throw new IllegalStateException("Duplicate uniform found");

        uniforms.put(name, setter);
    }

    public void registerUniforms(BaseShader baseShader) {
        for (Map.Entry<String, BaseShader.Setter> uniformEntry : uniforms.entrySet()) {
            baseShader.register(uniformEntry.getKey(), uniformEntry.getValue());
        }
    }
}
