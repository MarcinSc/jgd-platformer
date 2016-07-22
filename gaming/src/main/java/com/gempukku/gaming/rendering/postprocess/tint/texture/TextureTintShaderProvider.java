package com.gempukku.gaming.rendering.postprocess.tint.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector2;

public class TextureTintShaderProvider implements ShaderProvider {
    private TextureTintShader tintShader;
    private int sourceTextureIndex;
    private int tintTextureIndex;
    private Vector2 tintTextureOrigin;
    private Vector2 tintTextureSize;
    private Vector2 repeatFactor = new Vector2(1, 1);
    private Vector2 tintShift = new Vector2(0, 0);
    private float factor;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setTintTextureIndex(int tintTextureIndex) {
        this.tintTextureIndex = tintTextureIndex;
    }

    public void setTintTextureOrigin(Vector2 tintTextureOrigin) {
        this.tintTextureOrigin = tintTextureOrigin;
    }

    public void setTintTextureSize(Vector2 tintTextureSize) {
        this.tintTextureSize = tintTextureSize;
    }

    public void setRepeatFactor(Vector2 repeatFactor) {
        this.repeatFactor = repeatFactor;
    }

    public void setTintShift(Vector2 tintShift) {
        this.tintShift = tintShift;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (tintShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/viewToScreenCoords.vert").readString(),
                    Gdx.files.internal("shader/textureTint.frag").readString());
            tintShader = new TextureTintShader(renderable,
                    config);
            tintShader.init();
        }
        tintShader.setSourceTextureIndex(sourceTextureIndex);
        tintShader.setTintTextureIndex(tintTextureIndex);
        tintShader.setTintTextureOrigin(tintTextureOrigin);
        tintShader.setTintTextureSize(tintTextureSize);
        tintShader.setRepeatFactor(repeatFactor);
        tintShader.setTintShift(tintShift);
        tintShader.setFactor(factor);

        return tintShader;
    }

    @Override
    public void dispose() {
        if (tintShader != null)
            tintShader.dispose();
    }
}
