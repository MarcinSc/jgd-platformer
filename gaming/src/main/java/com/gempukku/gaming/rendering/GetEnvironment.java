package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.gempukku.secsy.entity.event.Event;

public class GetEnvironment extends Event {
    private Camera camera;

    private Environment environment;

    public GetEnvironment(Camera camera) {
        this.camera = camera;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Camera getCamera() {
        return camera;
    }
}
