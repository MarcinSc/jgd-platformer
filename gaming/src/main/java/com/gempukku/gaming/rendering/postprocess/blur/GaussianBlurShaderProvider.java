package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class GaussianBlurShaderProvider implements ShaderProvider {
    public static final int MAX_BLUR_RADIUS = 16;
    private static final float[][] kernelCache = new float[1 + MAX_BLUR_RADIUS][];

    private GaussianBlurShader blurShader;
    private int sourceTextureIndex;
    private int blurRadius;
    private boolean vertical;

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setBlurRadius(int blurRadius) {
        if (blurRadius > MAX_BLUR_RADIUS)
            blurRadius = MAX_BLUR_RADIUS;
        this.blurRadius = blurRadius;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (blurShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/viewToScreenCoords.vert").readString(),
                    Gdx.files.internal("shader/gaussianBlur.frag").readString());
            blurShader = new GaussianBlurShader(renderable,
                    config);
            blurShader.init();
        }
        blurShader.setSourceTextureIndex(sourceTextureIndex);
        blurShader.setBlurRadius(blurRadius);
        blurShader.setVertical(vertical);
        blurShader.setKernel(getKernel(blurRadius));

        return blurShader;
    }

    private static float[] getKernel(int blurRadius) {
        if (kernelCache[blurRadius] == null) {
            float[] kernel = GaussianBlurKernel.create1DBlurKernel(blurRadius);
            kernelCache[blurRadius] = new float[1 + MAX_BLUR_RADIUS];
            System.arraycopy(kernel, 0, kernelCache[blurRadius], 0, kernel.length);
        }
        return kernelCache[blurRadius];
    }

    @Override
    public void dispose() {
        if (blurShader != null)
            blurShader.dispose();
    }
}
