package com.gempukku.gaming.rendering.postprocess.bloom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class BloomShaderProvider implements ShaderProvider {
    private BloomShader bloomShader;
    private int sourceTextureIndex;
    private float blurRadius;
    private float minimalBrightness;
    private float bloomStrength;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setBlurRadius(float blurRadius) {
        this.blurRadius = blurRadius;
    }

    public void setMinimalBrightness(float minimalBrightness) {
        this.minimalBrightness = minimalBrightness;
    }

    public void setBloomStrength(float bloomStrength) {
        this.bloomStrength = bloomStrength;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (bloomShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/viewToScreenCoords.vert").readString(),
                    Gdx.files.internal("shader/bloom.frag").readString());
            bloomShader = new BloomShader(renderable,
                    config);
            bloomShader.init();
        }
        bloomShader.setSourceTextureIndex(sourceTextureIndex);
        bloomShader.setBlurRadius(blurRadius);
        bloomShader.setMinimalBrightness(minimalBrightness);
        bloomShader.setBloomStrength(bloomStrength);
        return bloomShader;
    }

    @Override
    public void dispose() {
        if (bloomShader != null)
            bloomShader.dispose();
    }
}
