package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;

public interface PluggableShaderBuilder {
    String getShaderIdentifier(Renderable renderable);

    PluggableShader buildShader(Renderable renderable);
}
