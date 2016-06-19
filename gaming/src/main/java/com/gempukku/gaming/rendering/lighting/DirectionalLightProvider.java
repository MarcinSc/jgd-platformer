package com.gempukku.gaming.rendering.lighting;

import com.badlogic.gdx.graphics.Camera;

public interface DirectionalLightProvider {
    /**
     * Sets up light camera information.
     *
     * @param lightCamera
     */
    void setupLightCamera(Camera lightCamera);

    float getAmbientLight();
}
