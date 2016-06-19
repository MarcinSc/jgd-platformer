package com.gempukku.gaming.rendering.postprocess;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.rendering.RenderingBuffer;
import com.gempukku.secsy.entity.EntityRef;

public interface PostProcessingRenderer {
    boolean isEnabled(EntityRef observerEntity);

    void render(EntityRef observerEntity, RenderingBuffer renderingBuffer, Camera camera, int sourceBoundColorTexture, int sourceBoundDepthTexture);
}
