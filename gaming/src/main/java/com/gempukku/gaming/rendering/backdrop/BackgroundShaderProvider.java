package com.gempukku.gaming.rendering.backdrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector3;

public class BackgroundShaderProvider implements ShaderProvider {
    private BackgroundShader backgroundShader;
    private Vector3 backgroundColor;

    public void setBackgroundColor(Vector3 backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (backgroundShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/background.vert").readString(),
                    Gdx.files.internal("shader/background.frag").readString());
            backgroundShader = new BackgroundShader(renderable,
                    config);
            backgroundShader.init();
        }
        backgroundShader.setBackgroundColor(backgroundColor);
        return backgroundShader;
    }

    @Override
    public void dispose() {
        if (backgroundShader != null)
            backgroundShader.dispose();
    }
}
