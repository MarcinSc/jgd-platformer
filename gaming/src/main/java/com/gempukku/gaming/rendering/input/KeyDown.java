package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class KeyDown extends Event {
    private int keyCode;

    public KeyDown(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
