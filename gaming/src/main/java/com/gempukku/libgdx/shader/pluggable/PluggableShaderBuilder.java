package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;

public interface PluggableShaderBuilder {
    void getShaderFeatures(Renderable renderable, PluggableShaderFeatures shaderFeatures);

    PluggableShader buildShader(Renderable renderable);
}
