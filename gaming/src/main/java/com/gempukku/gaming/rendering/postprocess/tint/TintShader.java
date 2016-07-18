package com.gempukku.gaming.rendering.postprocess.tint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class TintShader extends DefaultShader {
    private final int u_sourceTexture = register("u_sourceTexture");
    private final int u_color = register("u_color");
    private final int u_factor = register("u_factor");

    private int sourceTextureIndex;
    private Color color;
    private float factor;

    public TintShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

    public void setSourceTextureIndex(int sourceTextureIndex) {
        this.sourceTextureIndex = sourceTextureIndex;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_sourceTexture, sourceTextureIndex);
        set(u_color, color);
        set(u_factor, factor);
    }
}