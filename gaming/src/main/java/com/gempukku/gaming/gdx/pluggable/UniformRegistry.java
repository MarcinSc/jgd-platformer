package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

public interface UniformRegistry {
    void registerUniform(String name, BaseShader.Setter setter);
}
