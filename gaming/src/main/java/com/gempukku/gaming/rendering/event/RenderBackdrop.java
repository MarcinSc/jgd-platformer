package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.event.Event;

public class RenderBackdrop extends Event {
    private Camera camera;

    public RenderBackdrop(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}
