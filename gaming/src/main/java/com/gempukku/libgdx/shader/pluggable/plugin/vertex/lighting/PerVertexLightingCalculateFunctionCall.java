package com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public interface PerVertexLightingCalculateFunctionCall {
    String getFunctionName(Renderable renderable, boolean hasSpecular);

    void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular);

    void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder, boolean hasSpecular);

    boolean isProcessing(Renderable renderable, boolean hasSpecular);
}
