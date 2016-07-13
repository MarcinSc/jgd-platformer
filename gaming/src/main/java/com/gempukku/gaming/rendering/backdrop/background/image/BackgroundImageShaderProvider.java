package com.gempukku.gaming.rendering.backdrop.background.image;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector3;

public class BackgroundImageShaderProvider implements ShaderProvider {
    private BackgroundImageShader backgroundShader;
    private int backgroundImageIndex;
    private Vector3 backgroundColor;

    private float backgroundImageStartX;
    private float backgroundImageStartY;
    private float backgroundImageWidth;
    private float backgroundImageHeight;

    private float leftEdge;
    private float topEdge;
    private float rightEdge;
    private float bottomEdge;

    public void setBackgroundImageIndex(int backgroundImageIndex) {
        this.backgroundImageIndex = backgroundImageIndex;
    }

    public void setBackgroundColor(Vector3 backgroundColor) {
        this.backgroundColor = backgroundColor;
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

    public void setLeftEdge(float leftEdge) {
        this.leftEdge = leftEdge;
    }

    public void setTopEdge(float topEdge) {
        this.topEdge = topEdge;
    }

    public void setRightEdge(float rightEdge) {
        this.rightEdge = rightEdge;
    }

    public void setBottomEdge(float bottomEdge) {
        this.bottomEdge = bottomEdge;
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
        backgroundShader.setBackgroundColor(backgroundColor);

        backgroundShader.setBackgroundImageStartX(backgroundImageStartX);
        backgroundShader.setBackgroundImageStartY(backgroundImageStartY);
        backgroundShader.setBackgroundImageWidth(backgroundImageWidth);
        backgroundShader.setBackgroundImageHeight(backgroundImageHeight);

        backgroundShader.setLeftEdge(leftEdge);
        backgroundShader.setTopEdge(topEdge);
        backgroundShader.setRightEdge(rightEdge);
        backgroundShader.setBottomEdge(bottomEdge);

        return backgroundShader;
    }

    @Override
    public void dispose() {
        if (backgroundShader != null)
            backgroundShader.dispose();
    }
}
