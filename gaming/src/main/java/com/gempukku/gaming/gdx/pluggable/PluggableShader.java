package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PluggableShader extends BaseShader {
    private ShaderProgram shaderProgram;
    private Renderable renderable;

    public PluggableShader(ShaderProgram shaderProgram, Renderable renderable) {
        this.shaderProgram = shaderProgram;
        this.renderable = renderable;
    }

    @Override
    public void init() {
        super.init(shaderProgram, renderable);
    }

    @Override
    public int compareTo(Shader other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canRender(Renderable instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
        super.dispose();
    }
}
