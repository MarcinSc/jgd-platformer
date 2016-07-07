package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.event.Event;

public class PostRenderEnvironment extends Event {
    private Camera camera;

    public PostRenderEnvironment(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}
