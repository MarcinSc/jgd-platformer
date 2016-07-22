package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector2;

public class GaussianBlurShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_pixelSize = register("u_pixelSize");
    private final int u_blurRadius = register("u_blurRadius");
    private final int u_kernel;
    private final int u_vertical = register("u_vertical");

    private int sourceTextureIndex;
    private int blurRadius;
    private float[] kernel;
    private boolean vertical;
    private Vector2 pixelSize = new Vector2();

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
        pixelSize.set(1 / camera.viewportWidth, 1 / camera.viewportHeight);
        set(u_pixelSize, pixelSize);
        set(u_blurRadius, blurRadius);
        set(u_vertical, vertical ? 1 : 0);
        program.setUniform1fv(u_kernel, kernel, 0, kernel.length);
    }
}