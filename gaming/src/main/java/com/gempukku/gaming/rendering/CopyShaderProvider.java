package com.gempukku.gaming.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector2;

public class CopyShaderProvider implements ShaderProvider {
    private CopyShader copyShader;
    private int sourceTextureIndex;
    private Vector2 textureStart = new Vector2(0, 0);
    private Vector2 textureSize = new Vector2(1, 1);

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setTextureSize(float width, float height) {
        this.textureSize.set(width, height);
    }

    public void setTextureStart(float x, float y) {
        this.textureStart.set(x, y);
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (copyShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/viewToScreenCoords.vert").readString(),
                    Gdx.files.internal("shader/copy.frag").readString());
            copyShader = new CopyShader(renderable, config);
            copyShader.init();
        }
        copyShader.setSourceTextureIndex(sourceTextureIndex);
        copyShader.setTextureStart(textureStart);
        copyShader.setTextureSize(textureSize);
        return copyShader;
    }

    @Override
    public void dispose() {
        if (copyShader != null)
            copyShader.dispose();
    }
}
