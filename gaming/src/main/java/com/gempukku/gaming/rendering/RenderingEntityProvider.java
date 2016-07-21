package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.gempukku.secsy.entity.EntityRef;

public interface RenderingEntityProvider {
    EntityRef getRenderingEntity();

    void setupRenderingCamera(Camera camera);

    default Environment getEnvironment() {
        return null;
    }
}
