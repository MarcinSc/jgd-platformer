package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ColorAttributeCall implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("colorAttribute:");
    }

    @Override
    public String getFunctionName() {
        return "setColorVariable";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_color", "vec4");
        vertexShaderBuilder.addVaryingVariable("v_color", "vec4");
        vertexShaderBuilder.addFunction("setColorVariable",
                "void setColorVariable() {\n" +
                        "  v_color = a_color;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        long mask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return (mask & (VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.ColorPacked)) != 0;
    }
}
