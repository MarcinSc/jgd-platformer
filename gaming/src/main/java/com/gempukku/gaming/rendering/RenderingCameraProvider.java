package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.secsy.entity.EntityRef;

public interface RenderingCameraProvider {
    void setupRenderingCamera(EntityRef renderingEntity, Camera camera);
}
