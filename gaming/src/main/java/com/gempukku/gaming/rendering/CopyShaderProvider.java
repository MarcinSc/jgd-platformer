package com.gempukku.gaming.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class CopyShaderProvider implements ShaderProvider {
    private CopyShader copyShader;
    private int sourceTextureIndex;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (copyShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/copy.vert").readString(),
                    Gdx.files.internal("shader/copy.frag").readString());
            copyShader = new CopyShader(renderable, config);
            copyShader.init();
        }
        copyShader.setSourceTextureIndex(sourceTextureIndex);
        return copyShader;
    }

    @Override
    public void dispose() {
        if (copyShader != null)
            copyShader.dispose();
    }
}
