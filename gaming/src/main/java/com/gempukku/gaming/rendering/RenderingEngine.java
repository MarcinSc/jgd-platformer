package com.gempukku.gaming.rendering;

import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.entity.EntityRef;

public interface RenderingEngine {
    void render();

    void screenResized(int width, int height);

    void renderEntity(RenderPipeline renderPipeline, EntityRef renderingEntity, int width, int height);
}
