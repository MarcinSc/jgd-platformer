package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;

public interface PluggableVertexFunctionCall {
    String getFunctionName(Renderable renderable);

    void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures);

    void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder);

    boolean isProcessing(Renderable renderable);
}
