package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector2;

public class CopyShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_textureStart = register("u_textureStart");
    private final int u_textureSize = register("u_textureSize");

    private int sourceTextureIndex;
    private Vector2 textureStart;
    private Vector2 textureSize;

    public CopyShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setTextureSize(Vector2 textureSize) {
        this.textureSize = textureSize;
    }

    public void setTextureStart(Vector2 textureStart) {
        this.textureStart = textureStart;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_sourceTexture, sourceTextureIndex);
        set(u_textureStart, textureStart);
        set(u_textureSize, textureSize);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}