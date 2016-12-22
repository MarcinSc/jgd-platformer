package com.gempukku.libgdx.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicShader implements Shader, UniformRegistry {
    private static class StructArrayUniform {
        private final String alias;
        private final String[] fieldNames;
        private final boolean global;
        private final StructArrayUniformSetter setter;
        private int startIndex;
        private int size;
        private int[] fieldOffsets;

        public StructArrayUniform(String alias, String[] fieldNames, boolean global, StructArrayUniformSetter setter) {
            this.alias = alias;
            this.fieldNames = new String[fieldNames.length];
            System.arraycopy(fieldNames, 0, this.fieldNames, 0, fieldNames.length);
            this.global = global;
            this.setter = setter;
        }

        private void setUniformLocations(int startIndex, int size, int[] fieldOffsets) {
            this.startIndex = startIndex;
            this.size = size;
            this.fieldOffsets = fieldOffsets;
        }
    }

    private static class Uniform {
        private final String alias;
        private final boolean global;
        private final UniformSetter setter;
        private int location = -1;

        public Uniform(String alias, boolean global, UniformSetter setter) {
            this.alias = alias;
            this.global = global;
            this.setter = setter;
        }

        private void setUniformLocation(int location) {
            this.location = location;
        }
    }

    private final Map<String, Uniform> uniforms = new HashMap<>();
    private final Map<String, StructArrayUniform> structArrayUniforms = new HashMap<>();
    private final IntIntMap attributes = new IntIntMap();

    private ShaderProgram program;
    protected RenderContext context;
    public Camera camera;
    private Mesh currentMesh;

    private boolean initialized = false;

    @Override
    public void registerUniform(final String alias, final boolean global, final UniformSetter setter) {
        if (initialized) throw new GdxRuntimeException("Cannot register an uniform after initialization");
        validateNewUniform(alias);
        uniforms.put(alias, new Uniform(alias, global, setter));
    }

    @Override
    public void registerStructArrayUniform(final String alias, String[] fieldNames, final boolean global, StructArrayUniformSetter setter) {
        if (initialized) throw new GdxRuntimeException("Cannot register an uniform after initialization");
        validateNewUniform(alias);
        structArrayUniforms.put(alias, new StructArrayUniform(alias, fieldNames, global, setter));
    }

    private void validateNewUniform(String alias) {
        if (uniforms.containsKey(alias) || structArrayUniforms.containsKey(alias))
            throw new GdxRuntimeException("Uniform already registered");
    }

    /**
     * Initialize this shader, causing all registered uniforms/attributes to be fetched.
     */
    protected void init(final ShaderProgram program, final Renderable renderable) {
        if (initialized) throw new GdxRuntimeException("Already initialized");
        if (!program.isCompiled()) throw new GdxRuntimeException(program.getLog());
        this.program = program;

        for (Uniform uniform : uniforms.values()) {
            String alias = uniform.alias;
            int location = getUniformLocationSafely(program, alias);
            uniform.setUniformLocation(location);
        }

        for (StructArrayUniform uniform : structArrayUniforms.values()) {
            int startIndex = getUniformLocationSafely(program, uniform.alias + "[0]." + uniform.fieldNames[0]);
            int size = program.fetchUniformLocation(uniform.alias + "[1]." + uniform.fieldNames[0], false) - startIndex;
            int[] fieldOffsets = new int[uniform.fieldNames.length];
            // Starting at 1, as first field offset is 0 by default
            for (int i = 1; i < uniform.fieldNames.length; i++) {
                fieldOffsets[i] = getUniformLocationSafely(program, uniform.alias + "[0]." + uniform.fieldNames[i]) - startIndex;
            }
            uniform.setUniformLocations(startIndex, size, fieldOffsets);
        }

        if (renderable != null) {
            final VertexAttributes attrs = renderable.meshPart.mesh.getVertexAttributes();
            final int c = attrs.size();
            for (int i = 0; i < c; i++) {
                final VertexAttribute attr = attrs.get(i);
                final int location = program.getAttributeLocation(attr.alias);
                if (location >= 0) attributes.put(attr.getKey(), location);
            }
        }
        initialized = true;
    }

    private int getUniformLocationSafely(ShaderProgram program, String alias) {
        int location = program.fetchUniformLocation(alias, false);
        if (location == -1)
            throw new GdxRuntimeException("Uniform not found in program - " + alias);
        return location;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        currentMesh = null;
        for (Uniform uniform : uniforms.values()) {
            if (uniform.global)
                uniform.setter.set(this, uniform.location, null, null);
        }
        for (StructArrayUniform uniform : structArrayUniforms.values()) {
            if (uniform.global)
                uniform.setter.set(this, uniform.startIndex, uniform.fieldOffsets, uniform.size, null, null);
        }
    }

    private final IntArray tempArray = new IntArray();

    private final int[] getAttributeLocations(final VertexAttributes attrs) {
        tempArray.clear();
        final int n = attrs.size();
        for (int i = 0; i < n; i++) {
            tempArray.add(attributes.get(attrs.get(i).getKey(), -1));
        }
        return tempArray.items;
    }

    private Attributes combinedAttributes = new Attributes();

    @Override
    public void render(Renderable renderable) {
        if (renderable.worldTransform.det3x3() == 0) return;
        combinedAttributes.clear();
        if (renderable.environment != null) combinedAttributes.set(renderable.environment);
        if (renderable.material != null) combinedAttributes.set(renderable.material);
        render(renderable, combinedAttributes);
    }

    public void render(Renderable renderable, final Attributes combinedAttributes) {
        for (Uniform uniform : uniforms.values()) {
            if (!uniform.global)
                uniform.setter.set(this, uniform.location, renderable, combinedAttributes);
        }
        for (StructArrayUniform uniform : structArrayUniforms.values()) {
            if (!uniform.global)
                uniform.setter.set(this, uniform.startIndex, uniform.fieldOffsets, uniform.size, renderable, combinedAttributes);
        }

        if (currentMesh != renderable.meshPart.mesh) {
            if (currentMesh != null) currentMesh.unbind(program, tempArray.items);
            currentMesh = renderable.meshPart.mesh;
            currentMesh.bind(program, getAttributeLocations(renderable.meshPart.mesh.getVertexAttributes()));
        }
        renderable.meshPart.render(program, false);
    }

    @Override
    public void end() {
        if (currentMesh != null) {
            currentMesh.unbind(program, tempArray.items);
            currentMesh = null;
        }
        program.end();
    }

    @Override
    public void dispose() {
        program = null;
        uniforms.clear();
        structArrayUniforms.clear();
        attributes.clear();
    }

    public void setUniform(final int location, final Matrix4 value) {
        program.setUniformMatrix(location, value);
    }

    public void setUniform(final int location, final Matrix3 value) {
        program.setUniformMatrix(location, value);
    }

    public void setUniform(final int location, final Vector3 value) {
        program.setUniformf(location, value);
    }

    public void setUniform(final int location, final Vector2 value) {
        program.setUniformf(location, value);
    }

    public void setUniform(final int location, final Color value) {
        program.setUniformf(location, value);
    }

    public void setUniform(final int location, final float value) {
        program.setUniformf(location, value);
    }

    public void setUniform(final int location, final float v1, final float v2) {
        program.setUniformf(location, v1, v2);
    }

    public void setUniform(final int location, final float v1, final float v2, final float v3) {
        program.setUniformf(location, v1, v2, v3);
    }

    public void setUniform(final int location, final float v1, final float v2, final float v3, final float v4) {
        program.setUniformf(location, v1, v2, v3, v4);
    }

    public void setUniform(final int location, final int value) {
        program.setUniformi(location, value);
    }

    public void setUniform(final int location, final int v1, final int v2) {
        program.setUniformi(location, v1, v2);
    }

    public void setUniform(final int location, final int v1, final int v2, final int v3) {
        program.setUniformi(location, v1, v2, v3);
    }

    public void setUniform(final int location, final int v1, final int v2, final int v3, final int v4) {
        program.setUniformi(location, v1, v2, v3, v4);
    }

    public void setUniform(final int location, final TextureDescriptor textureDesc) {
        program.setUniformi(location, context.textureBinder.bind(textureDesc));
    }

    public void setUniform(final int location, final GLTexture texture) {
        program.setUniformi(location, context.textureBinder.bind(texture));
    }

    public void setUniformMatrixArray(final int location, float[] values) {
        program.setUniformMatrix4fv(location, values, 0, values.length);
    }

    public void setUniformArray(final int location, float[] values) {
        program.setUniform3fv(location, values, 0, values.length);
    }
}
