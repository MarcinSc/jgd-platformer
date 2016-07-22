package com.gempukku.gaming.rendering.postprocess.tint.texture;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector2;

public class TextureTintShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_tintTexture = register("u_tintTexture");
    private final int u_tintTextureOrigin = register("u_tintTextureOrigin");
    private final int u_tintTextureSize = register("u_tintTextureSize");
    private final int u_factor = register("u_factor");

    private int sourceTextureIndex;
    private int tintTextureIndex;
    private Vector2 tintTextureOrigin;
    private Vector2 tintTextureSize;
    private float factor;

    public TextureTintShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

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

    public void setFactor(float factor) {
        this.factor = factor;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_sourceTexture, sourceTextureIndex);
        set(u_tintTexture, tintTextureIndex);
        set(u_tintTextureOrigin, tintTextureOrigin);
        set(u_tintTextureSize, tintTextureSize);
        set(u_factor, factor);
    }
}