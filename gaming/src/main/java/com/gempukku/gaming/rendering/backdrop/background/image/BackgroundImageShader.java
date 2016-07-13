package com.gempukku.gaming.rendering.backdrop.background.image;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;

public class BackgroundImageShader extends DefaultShader {
    private final int u_backgroundTexture = register("u_backgroundTexture");
    private final int u_backgroundColor = register("u_backgroundColor");

    private final int u_imageStartX = register("u_imageStartX");
    private final int u_imageStartY = register("u_imageStartY");
    private final int u_imageWidth = register("u_imageWidth");
    private final int u_imageHeight = register("u_imageHeight");

    private final int u_leftEdge = register("u_leftEdge");
    private final int u_topEdge = register("u_topEdge");
    private final int u_rightEdge = register("u_rightEdge");
    private final int u_bottomEdge = register("u_bottomEdge");

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

    public void setBackgroundColor(Vector3 backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBottomEdge(float bottomEdge) {
        this.bottomEdge = bottomEdge;
    }

    public void setLeftEdge(float leftEdge) {
        this.leftEdge = leftEdge;
    }

    public void setRightEdge(float rightEdge) {
        this.rightEdge = rightEdge;
    }

    public void setTopEdge(float topEdge) {
        this.topEdge = topEdge;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_backgroundTexture, backgroundImageIndex);
        set(u_backgroundColor, backgroundColor);

        set(u_imageStartX, backgroundImageStartX);
        set(u_imageStartY, backgroundImageStartY);
        set(u_imageWidth, backgroundImageWidth);
        set(u_imageHeight, backgroundImageHeight);

        set(u_leftEdge, leftEdge);
        set(u_topEdge, topEdge);
        set(u_rightEdge, rightEdge);
        set(u_bottomEdge, bottomEdge);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}