package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;

public class BlendingAttributeTransform implements PluggableFragmentFunctionCall {
    @Override
    public String getFunctionName() {
        return "getBlendedColor";
    }

    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("blendingTransform:");
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        boolean hasAlphaTest = renderable.material.has(FloatAttribute.AlphaTest);

        fragmentShaderBuilder.addVaryingVariable("v_opacity", "float");
        if (hasAlphaTest)
            fragmentShaderBuilder.addVaryingVariable("v_alphaTest", "float");
        StringBuilder function = new StringBuilder();
        function.append(
                "vec4 getBlendedColor(vec4 color) {\n" +
                        "  color.a = color.a * v_opacity;\n");
        if (hasAlphaTest)
            function.append(
                    "  if (color.a <= v_alphaTest)\n" +
                            "    discard;\n");
        function.append(
                "  return color;\n" +
                        "}\n");
        fragmentShaderBuilder.addFunction("getBlendedColor", function.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(BlendingAttribute.Type);
    }
}
