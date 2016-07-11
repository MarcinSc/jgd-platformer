package com.gempukku.gaming.rendering.backdrop.background.image;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class BackgroundImageShaderProvider implements ShaderProvider {
    private BackgroundImageShader backgroundShader;
    private int backgroundImageIndex;
    private float backgroundImageStartX;
    private float backgroundImageStartY;
    private float backgroundImageWidth;
    private float backgroundImageHeight;

    public void setBackgroundImageIndex(int backgroundImageIndex) {
        this.backgroundImageIndex = backgroundImageIndex;
    }

    public void setBackgroundImageHeight(float backgroundImageHeight) {
        this.backgroundImageHeight = backgroundImageHeight;
    }

    public void setBackgroundImageStartX(float backgroundImageStartX) {
        this.backgroundImageStartX = backgroundImageStartX;
    }

    public void setBackgroundImageStartY(float backgroundImageStartY) {
        this.backgroundImageStartY = backgroundImageStartY;
    }

    public void setBackgroundImageWidth(float backgroundImageWidth) {
        this.backgroundImageWidth = backgroundImageWidth;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        if (backgroundShader == null) {
            DefaultShader.Config config = new DefaultShader.Config(
                    Gdx.files.internal("shader/backgroundImage.vert").readString(),
                    Gdx.files.internal("shader/backgroundImage.frag").readString());
            backgroundShader = new BackgroundImageShader(renderable,
                    config);
            backgroundShader.init();
        }
        backgroundShader.setBackgroundImageIndex(backgroundImageIndex);
        backgroundShader.setBackgroundImageStartX(backgroundImageStartX);
        backgroundShader.setBackgroundImageStartY(backgroundImageStartY);
        backgroundShader.setBackgroundImageWidth(backgroundImageWidth);
        backgroundShader.setBackgroundImageHeight(backgroundImageHeight);
        return backgroundShader;
    }

    @Override
    public void dispose() {
        if (backgroundShader != null)
            backgroundShader.dispose();
    }
}
