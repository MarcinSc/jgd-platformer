package com.gempukku.gaming.rendering.postprocess.tint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class TintShaderProvider implements ShaderProvider {
    private TintShader tintShader;
    private int sourceTextureIndex;
    private Color color;
    private float factor;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (tintShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/viewToScreenCoords.vert").readString(),
                    Gdx.files.internal("shader/tint.frag").readString());
            tintShader = new TintShader(renderable,
                    config);
            tintShader.init();
        }
        tintShader.setSourceTextureIndex(sourceTextureIndex);
        tintShader.setColor(color);
        tintShader.setFactor(factor);

        return tintShader;
    }

    @Override
    public void dispose() {
        if (tintShader != null)
            tintShader.dispose();
    }
}
