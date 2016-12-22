package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public interface PerVertexLightingApplyFunctionCall {
    String getFunctionName(Renderable renderable, boolean hasSpecular);

    void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular);

    void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular);

    boolean isProcessing(Renderable renderable, boolean hasSpecular);
}
