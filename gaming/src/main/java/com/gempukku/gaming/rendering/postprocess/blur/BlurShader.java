package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class BlurShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_viewportWidth = register("u_viewportWidth");
    private final int u_viewportHeight = register("u_viewportHeight");
    private final int u_blurRadius = register("u_blurRadius");

    private int sourceTextureIndex;
    private float blurRadius;

    public BlurShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setBlurRadius(float blurRadius) {
        this.blurRadius = blurRadius;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_sourceTexture, sourceTextureIndex);
        set(u_viewportWidth, camera.viewportWidth);
        set(u_viewportHeight, camera.viewportHeight);
        set(u_blurRadius, blurRadius);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}