package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;

public interface PluggableVertexFunctionCall {
    String getFunctionName();

    void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder);

    void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder);

    boolean isProcessing(Renderable renderable);
}
