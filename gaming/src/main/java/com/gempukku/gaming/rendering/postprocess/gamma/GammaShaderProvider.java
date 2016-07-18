package com.gempukku.gaming.rendering.postprocess.gamma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class GammaShaderProvider implements ShaderProvider {
    private GammaShader tintShader;
    private int sourceTextureIndex;
    private float factor;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (tintShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/viewToScreenCoords.vert").readString(),
                    Gdx.files.internal("shader/gamma.frag").readString());
            tintShader = new GammaShader(renderable,
                    config);
            tintShader.init();
        }
        tintShader.setSourceTextureIndex(sourceTextureIndex);
        tintShader.setFactor(factor);

        return tintShader;
    }

    @Override
    public void dispose() {
        if (tintShader != null)
            tintShader.dispose();
    }
}
