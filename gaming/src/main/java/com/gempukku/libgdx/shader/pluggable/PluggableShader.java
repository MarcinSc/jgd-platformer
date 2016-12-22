package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.gempukku.libgdx.shader.UniformCachingShader;

public class PluggableShader extends UniformCachingShader {
    private ShaderProgram shaderProgram;
    private Renderable renderable;

    public PluggableShader(Renderable renderable) {
        this.renderable = renderable;
    }

    public void setProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
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
    public void init() {
        init(shaderProgram, renderable);
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
        super.dispose();
    }
}
