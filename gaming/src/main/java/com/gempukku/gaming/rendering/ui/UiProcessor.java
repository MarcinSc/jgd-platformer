package com.gempukku.gaming.rendering.ui;

import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;

public interface UiProcessor extends InputProcessor {
    void processUi(InputEventQueue inputEventQueue);
    void processUi();
}
