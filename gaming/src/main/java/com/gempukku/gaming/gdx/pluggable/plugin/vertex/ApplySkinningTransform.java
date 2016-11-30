package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ApplySkinningTransform implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        if (isProcessing(renderable))
            stringBuilder.append("applySkinning").append(renderable.bones.length).append(':');
    }

    @Override
    public String getFunctionName() {
        return "transformWithSkinning";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addFunction("transformWithSkinning",
                "vec4 transformWithSkinning(vec4 position) {\n" +
                        "  return skinning * position;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }
}
