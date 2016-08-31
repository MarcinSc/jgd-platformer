package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.entity.EntityRef;

public interface RenderingEngine {
    void render();

    void screenResized(int width, int height);

    void renderEntity(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera, Environment environment);
}
