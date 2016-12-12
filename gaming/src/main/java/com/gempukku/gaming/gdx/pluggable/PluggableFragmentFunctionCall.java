package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;

public interface PluggableFragmentFunctionCall {
    String getFunctionName(Renderable renderable);

    void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures);

    void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder);

    boolean isProcessing(Renderable renderable);
}
