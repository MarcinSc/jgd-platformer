package com.gempukku.gaming.rendering.postprocess.bloom;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector2;

public class BloomShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_minimalBrightness = register("u_minimalBrightness");
    private final int u_pixelSize = register("u_pixelSize");
    private final int u_blurRadius = register("u_blurRadius");
    private final int u_bloomStrength = register("u_bloomStrength");

    private int sourceTextureIndex;
    private float blurRadius;
    private float minimalBrightness;
    private float bloomStrength;
    private Vector2 pixelSize = new Vector2();

    public BloomShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

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
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_sourceTexture, sourceTextureIndex);
        set(u_minimalBrightness, minimalBrightness);
        set(u_blurRadius, blurRadius);
        pixelSize.set(1 / camera.viewportWidth, 1 / camera.viewportHeight);
        set(u_pixelSize, pixelSize);
        set(u_bloomStrength, bloomStrength);
    }
}