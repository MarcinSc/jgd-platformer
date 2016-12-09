package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;

public class WhiteColorSource implements PluggableFragmentFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("white:");
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getWhite";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addFunction("getWhite",
                "vec4 getWhite() {\n" +
                        "  return vec4(1.0);\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
