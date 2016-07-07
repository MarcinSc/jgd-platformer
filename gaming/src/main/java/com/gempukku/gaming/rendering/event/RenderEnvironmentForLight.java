package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.event.Event;

public class RenderEnvironmentForLight extends Event {
    private Camera lightCamera;

    public RenderEnvironmentForLight(Camera lightCamera) {
        this.lightCamera = lightCamera;
    }

    public Camera getLightCamera() {
        return lightCamera;
    }
}
