package com.gempukku.gaming.rendering.event;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.rendering.postprocess.PostProcessPipeline;
import com.gempukku.secsy.entity.event.Event;

public class PostProcessRendering extends Event {
    private PostProcessPipeline postProcessPipeline;
    private Camera camera;

    public PostProcessRendering(PostProcessPipeline postProcessPipeline, Camera camera) {
        this.postProcessPipeline = postProcessPipeline;
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public PostProcessPipeline getPostProcessPipeline() {
        return postProcessPipeline;
    }
}
