package com.gempukku.libgdx.shader;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;

public interface UniformRegistry {
    void registerRenderInitializer(final boolean global, RenderInitializer renderInitializer);

    void registerUniform(final String alias, final boolean global, final UniformSetter setter);

    void registerStructArrayUniform(final String alias, String[] fieldNames, final boolean global, StructArrayUniformSetter setter);

    public interface RenderInitializer {
        void initialize(final BasicShader shader, final Renderable renderable, final Attributes combinedAttributes);
    }

    public interface UniformSetter {
        void set(final BasicShader shader, final int location, final Renderable renderable, final Attributes combinedAttributes);
    }

    public interface StructArrayUniformSetter {
        void set(final BasicShader shader, final int startingLocation, int[] fieldOffsets, int structSize, final Renderable renderable, final Attributes combinedAttributes);
    }
}
