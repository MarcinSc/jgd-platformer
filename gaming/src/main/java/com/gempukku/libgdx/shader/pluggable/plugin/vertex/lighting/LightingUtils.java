package com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.BasicShader;
import com.gempukku.libgdx.shader.UniformRegistry;

public class LightingUtils {
    public static final UniformRegistry.UniformSetter defaultShininessSetter = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            shader.setUniform(location, 20f);
        }
    };
}
