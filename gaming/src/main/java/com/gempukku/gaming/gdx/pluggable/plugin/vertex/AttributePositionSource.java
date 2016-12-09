package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class AttributePositionSource implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("attributePosition:");
    }

    @Override
    public String getFunctionName() {
        return "getPositionAttribute";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_position", "vec3");
        vertexShaderBuilder.addFunction("getPositionAttribute", "vec4 getPositionAttribute() {\n  return vec4(a_position, 1.0);\n}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}