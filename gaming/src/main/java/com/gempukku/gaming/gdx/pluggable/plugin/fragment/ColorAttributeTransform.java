package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;

public class ColorAttributeTransform implements PluggableFragmentFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        if (isProcessing(renderable))
            stringBuilder.append("colorTransform:");
    }

    @Override
    public String getFunctionName() {
        return "getTransformedColor";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addVaryingVariable("v_color", "vec4");
        fragmentShaderBuilder.addFunction("getTransformedColor",
                "vec4 getTransformedColor(vec4 color) {\n" +
                        "  return color * v_color;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        long mask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return (mask & (VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.ColorPacked)) != 0;
    }
}
