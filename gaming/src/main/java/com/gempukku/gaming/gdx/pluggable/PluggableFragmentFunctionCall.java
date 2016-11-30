package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;

public interface PluggableFragmentFunctionCall {
    String getFunctionName();

    void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder);

    void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder);

    boolean isProcessing(Renderable renderable);
}
