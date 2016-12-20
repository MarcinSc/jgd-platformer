package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

import java.util.HashMap;
import java.util.Map;

public class DefaultUniformRegistry implements UniformRegistry {
    private Map<String, BaseShader.Setter> uniforms = new HashMap<String, BaseShader.Setter>();
    private Map<StructArrayUniform, Setter> structArrayUniforms = new HashMap<>();

    @Override
    public void registerUniform(String name, BaseShader.Setter setter) {
        if (uniforms.containsKey(name))
            throw new IllegalStateException("Duplicate uniform found");

        uniforms.put(name, setter);
    }

    @Override
    public void registerStructArrayUniform(String name, String[] fieldNames, Setter setter) {
        if (structArrayUniforms.containsKey(name))
            throw new IllegalStateException("Duplicate uniform found");

        structArrayUniforms.put(new StructArrayUniform(name, fieldNames), setter);
    }

    public void registerUniforms(BaseShader baseShader) {
        for (Map.Entry<String, BaseShader.Setter> uniformEntry : uniforms.entrySet()) {
            baseShader.register(uniformEntry.getKey(), uniformEntry.getValue());
        }
        for (StructArrayUniform structArrayUniform : structArrayUniforms.keySet()) {
            baseShader.register(structArrayUniform.name + "[0]." + structArrayUniform.fieldNames[0]);
            baseShader.register(structArrayUniform.name + "[1]." + structArrayUniform.fieldNames[0]);
            for (int i = 1; i < structArrayUniform.fieldNames.length; i++) {
                baseShader.register(structArrayUniform.name + "[0]." + structArrayUniform.fieldNames[i]);
            }
        }
    }

    private void processLocations(BaseShader baseShader, StructArrayUniform structArrayUniform) {
        if (structArrayUniform.fieldOffsets == null) {
            int start = baseShader.loc(baseShader.getUniformID(structArrayUniform.name + "[0]." + structArrayUniform.fieldNames[0]));
            int size = baseShader.loc(baseShader.getUniformID(structArrayUniform.name + "[1]." + structArrayUniform.fieldNames[0])) - start;
            int[] fieldOffsets = new int[structArrayUniform.fieldNames.length];
            for (int i = 1; i < structArrayUniform.fieldNames.length; i++) {
                fieldOffsets[i] = baseShader.loc(baseShader.getUniformID(structArrayUniform.name + "[0]." + structArrayUniform.fieldNames[i])) - start;
            }

            structArrayUniform.startIndex = start;
            structArrayUniform.size = size;
            structArrayUniform.fieldOffsets = fieldOffsets;
        }
    }

    public void processGlobalStructArrayUniforms(BaseShader shader, Renderable renderable, Attributes combinedAttributes) {
        for (Map.Entry<StructArrayUniform, Setter> entry : structArrayUniforms.entrySet()) {
            Setter setter = entry.getValue();
            if (setter.isGlobal(shader)) {
                StructArrayUniform structArrayUniform = entry.getKey();
                processLocations(shader, structArrayUniform);
                setter.set(shader, structArrayUniform.startIndex, structArrayUniform.fieldOffsets, structArrayUniform.size, renderable, combinedAttributes);
            }
        }
    }

    public void processLocalStructArrayUniforms(BaseShader shader, Renderable renderable, Attributes combinedAttributes) {
        for (Map.Entry<StructArrayUniform, Setter> entry : structArrayUniforms.entrySet()) {
            Setter setter = entry.getValue();
            if (!setter.isGlobal(shader)) {
                StructArrayUniform structArrayUniform = entry.getKey();
                processLocations(shader, structArrayUniform);
                setter.set(shader, structArrayUniform.startIndex, structArrayUniform.fieldOffsets, structArrayUniform.size, renderable, combinedAttributes);
            }
        }
    }

    public static class StructArrayUniform {
        public String name;
        public String[] fieldNames;
        public int startIndex;
        public int size;
        public int[] fieldOffsets;

        public StructArrayUniform(String name, String[] fieldNames) {
            this.name = name;
            this.fieldNames = fieldNames;
        }

        public void setIndexValues(int startIndex, int size, int[] fieldOffsets) {
            this.startIndex = startIndex;
            this.size = size;
            this.fieldOffsets = fieldOffsets;
        }
    }
}
