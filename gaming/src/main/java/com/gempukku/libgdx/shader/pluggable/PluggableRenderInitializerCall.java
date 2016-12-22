package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.UniformRegistry;

public interface PluggableRenderInitializerCall {
    void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures);

    void appendInitializer(Renderable renderable, UniformRegistry uniformRegistry);

    boolean isProcessing(Renderable renderable);
}
