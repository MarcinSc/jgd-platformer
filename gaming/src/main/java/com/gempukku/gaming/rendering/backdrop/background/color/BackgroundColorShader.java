package com.gempukku.gaming.rendering.backdrop.background.color;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;

public class BackgroundColorShader extends DefaultShader {
    private final int u_backgroundColor = register("u_backgroundColor");

    private Vector3 backgroundColor;

    public BackgroundColorShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

    public void setBackgroundColor(Vector3 backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_backgroundColor, backgroundColor);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}