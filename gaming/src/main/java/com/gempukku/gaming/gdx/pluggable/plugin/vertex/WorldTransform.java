package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class WorldTransform implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("worldTransform:");
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "transformToWorld";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_worldTrans", "mat4", DefaultShader.Setters.worldTrans);
        vertexShaderBuilder.addFunction("transformToWorld",
                "vec4 transformToWorld(vec4 position) {\n" +
                        "  return u_worldTrans * position;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
