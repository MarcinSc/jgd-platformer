package com.gempukku.gaming.rendering.backdrop.background.image;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class BackgroundImageShader extends DefaultShader {
    private final int u_backgroundTexture = register("u_backgroundTexture");
    private final int u_backgroundImageStartX = register("u_backgroundImageStartX");
    private final int u_backgroundImageStartY = register("u_backgroundImageStartY");
    private final int u_backgroundImageWidth = register("u_backgroundImageWidth");
    private final int u_backgroundImageHeight = register("u_backgroundImageHeight");

    private int backgroundImageIndex;
    private float backgroundImageStartX;
    private float backgroundImageStartY;
    private float backgroundImageWidth;
    private float backgroundImageHeight;

    public BackgroundImageShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

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
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_backgroundTexture, backgroundImageIndex);
        set(u_backgroundImageStartX, backgroundImageStartX);
        set(u_backgroundImageStartY, backgroundImageStartY);
        set(u_backgroundImageWidth, backgroundImageWidth);
        set(u_backgroundImageHeight, backgroundImageHeight);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}