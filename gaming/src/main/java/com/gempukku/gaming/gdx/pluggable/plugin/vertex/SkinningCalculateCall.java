package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class SkinningCalculateCall implements PluggableVertexFunctionCall {
    @Override
    public void appendShaderIdentifier(Renderable renderable, StringBuilder stringBuilder) {
        stringBuilder.append("skinningCalculate").append(renderable.bones.length).append(':');
    }

    @Override
    public String getFunctionName() {
        return "calculateSkinning";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        final int n = renderable.meshPart.mesh.getVertexAttributes().size();
        for (int i = 0; i < n; i++) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == VertexAttributes.Usage.BoneWeight)
                vertexShaderBuilder.addAttributeVariable("a_boneWeight" + attr.unit, "vec2");
        }

        int boneCount = renderable.bones.length;
        vertexShaderBuilder.addArrayUniformVariable("u_bones", boneCount, "mat4", new DefaultShader.Setters.Bones(boneCount));

        vertexShaderBuilder.addVariable("skinning", "mat4");
        StringBuilder skinningFunction = new StringBuilder();
        skinningFunction.append(
                "void calculateSkinning() {\n");
        skinningFunction.append(
                "  skinning = mat4(0.0);\n");
        for (int i = 0; i < n; i++) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == VertexAttributes.Usage.BoneWeight) {
                skinningFunction.append(
                        "  skinning += (a_boneWeight" + attr.unit + ".y) * u_bones[int(a_boneWeight" + attr.unit + ".x)];\n");
            }
        }
        skinningFunction.append(
                "}\n");
        vertexShaderBuilder.addFunction("calculateSkinning", skinningFunction.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }
}
