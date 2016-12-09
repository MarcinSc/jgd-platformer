package com.gempukku.gaming.gdx.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.gempukku.gaming.gdx.pluggable.FragmentShaderBuilder;
import com.gempukku.gaming.gdx.pluggable.PluggableFragmentFunctionCall;

public class BlendingAttributeWithoutAlphaTestTransform implements PluggableFragmentFunctionCall {
    @Override
    public String getFunctionName() {
        return "getBlendedColorWithoutAlphaTest";
    }

    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("blendingTransformWithoutAlphaTest:");
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addVaryingVariable("v_opacity", "float");
        fragmentShaderBuilder.addVaryingVariable("v_alphaTest", "float");
        fragmentShaderBuilder.addFunction("getBlendedColorWithoutAlphaTest",
                "vec4 getBlendedColorWithoutAlphaTest(vec4 color) {\n" +
                        "  color.a = color.a * v_opacity;\n" +
                        "  return color;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.material.has(BlendingAttribute.Type) && !renderable.material.has(FloatAttribute.AlphaTest);
    }
}
