package com.gempukku.gaming.rendering.environment;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;

public interface EnvironmentRenderer {
    void renderEnvironmentForLight(Camera lightCamera);

    void renderEnvironment(
            boolean hasDirectionalLight,
            Camera camera, Camera lightCamera, Texture lightTexture, int shadowFidelity, float ambientLight);
}
