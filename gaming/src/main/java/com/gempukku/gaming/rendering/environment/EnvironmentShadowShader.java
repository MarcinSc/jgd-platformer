package com.gempukku.gaming.rendering.environment;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;

public class EnvironmentShadowShader extends DefaultShader {
    private final int u_lightDirection = register("u_lightDirection");
    private final int u_lightPlaneDistance = register("u_lightPlaneDistance");
    private final int u_time = register("u_time");

    private Vector3 lightDirection;
    private float lightPlaneDistance;
    private float time;

    public EnvironmentShadowShader(Renderable renderable, Config config) {
        super(renderable, config);
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setLightDirection(Vector3 lightDirection) {
        this.lightDirection = lightDirection;
    }

    public void setLightPlaneDistance(float lightPlaneDistance) {
        this.lightPlaneDistance = lightPlaneDistance;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(u_lightDirection, lightDirection);
        set(u_lightPlaneDistance, lightPlaneDistance);
        set(u_time, time);
    }
}
