package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class BlurShaderProvider implements ShaderProvider {
    private BlurShader blurShader;
    private int sourceTextureIndex;
    private float blurRadius;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setBlurRadius(float blurRadius) {
        this.blurRadius = blurRadius;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (blurShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/blur.vert").readString(),
                    Gdx.files.internal("shader/blur.frag").readString());
            blurShader = new BlurShader(renderable,
                    config);
            blurShader.init();
        }
        blurShader.setSourceTextureIndex(sourceTextureIndex);
        blurShader.setBlurRadius(blurRadius);
        return blurShader;
    }

    @Override
    public void dispose() {
        if (blurShader != null)
            blurShader.dispose();
    }
}
