package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.event.Event;

public class GetCamera extends Event {
    private float width;
    private float height;

    private Camera camera;

    public GetCamera(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
