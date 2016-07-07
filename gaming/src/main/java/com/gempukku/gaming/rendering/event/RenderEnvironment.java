package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.gempukku.secsy.entity.event.Event;

public class RenderEnvironment extends Event {
    private boolean hasDirectionalLight;
    private Camera camera;
    private Camera lightCamera;
    private Texture lightTexture;
    private int shadowFidelity;
    private float ambientLight;

    public RenderEnvironment(boolean hasDirectionalLight, Camera camera, Camera lightCamera, Texture lightTexture, int shadowFidelity, float ambientLight) {
        this.hasDirectionalLight = hasDirectionalLight;
        this.camera = camera;
        this.lightCamera = lightCamera;
        this.lightTexture = lightTexture;
        this.shadowFidelity = shadowFidelity;
        this.ambientLight = ambientLight;
    }

    public float getAmbientLight() {
        return ambientLight;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isHasDirectionalLight() {
        return hasDirectionalLight;
    }

    public Camera getLightCamera() {
        return lightCamera;
    }

    public Texture getLightTexture() {
        return lightTexture;
    }

    public int getShadowFidelity() {
        return shadowFidelity;
    }
}
