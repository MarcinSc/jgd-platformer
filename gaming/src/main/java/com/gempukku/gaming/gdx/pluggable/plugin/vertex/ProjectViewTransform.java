package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ProjectViewTransform implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("projectViewTransform:");
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "transformToProjectView";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_projViewTrans", "mat4", DefaultShader.Setters.projViewTrans);
        vertexShaderBuilder.addFunction("transformToProjectView",
                "vec4 transformToProjectView(vec4 position) {\n" +
                        "  return u_projViewTrans * position;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
