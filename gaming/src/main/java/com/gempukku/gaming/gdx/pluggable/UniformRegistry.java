package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

public interface UniformRegistry {
    void registerUniform(String name, BaseShader.Setter setter);

    void registerStructArrayUniform(String name, String[] fieldNames, Setter setter);

    public interface Setter {
        /**
         * @return True if the uniform only has to be set once per render call, false if the uniform must be set for each renderable.
         */
        boolean isGlobal(final BaseShader shader);

        void set(final BaseShader shader, final int startingLocation, int[] fieldOffsets, int structSize, final Renderable renderable, final Attributes combinedAttributes);
    }
}
