package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.rendering.FlipOffScreenRenderingBuffer;
import com.gempukku.secsy.entity.event.Event;

public class PostProcessRendering extends Event {
    private FlipOffScreenRenderingBuffer renderingBuffer;
    private Camera camera;

    public PostProcessRendering(FlipOffScreenRenderingBuffer renderingBuffer, Camera camera) {
        this.renderingBuffer = renderingBuffer;
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public FlipOffScreenRenderingBuffer getRenderingBuffer() {
        return renderingBuffer;
    }
}
