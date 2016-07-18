package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class GaussianBlurShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_viewportWidth = register("u_viewportWidth");
    private final int u_viewportHeight = register("u_viewportHeight");
    private final int u_blurRadius = register("u_blurRadius");
    private final int u_kernel;
    private final int u_vertical = register("u_vertical");

    private int sourceTextureIndex;
    private int blurRadius;
    private float[] kernel;
    private boolean vertical;

    public GaussianBlurShader(Renderable renderable, Config config) {
        super(renderable, config);
        u_kernel = program.fetchUniformLocation("u_kernel[0]", false);
    }

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setBlurRadius(int blurRadius) {
        this.blurRadius = blurRadius;
    }

    public void setKernel(float[] kernel) {
        this.kernel = kernel;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_sourceTexture, sourceTextureIndex);
        set(u_viewportWidth, camera.viewportWidth);
        set(u_viewportHeight, camera.viewportHeight);
        set(u_blurRadius, blurRadius);
        set(u_vertical, vertical ? 1 : 0);
        program.setUniform1fv(u_kernel, kernel, 0, kernel.length);
    }
}