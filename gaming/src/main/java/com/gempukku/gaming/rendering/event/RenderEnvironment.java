package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.entity.event.Event;

public class RenderEnvironment extends Event {
    private RenderPipeline renderPipeline;
    private Camera camera;
    private Environment environment;

    public RenderEnvironment(RenderPipeline renderPipeline, Environment environment, Camera camera) {
        this.renderPipeline = renderPipeline;
        this.environment = environment;
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public RenderPipeline getRenderPipeline() {
        return renderPipeline;
    }
}
