package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.entity.event.Event;

public class UiRendering extends Event {
    private RenderPipeline renderPipeline;
    private Camera camera;

    public UiRendering(RenderPipeline renderPipeline, Camera camera) {
        this.renderPipeline = renderPipeline;
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public RenderPipeline getRenderPipeline() {
        return renderPipeline;
    }

}
