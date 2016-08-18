package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class KeyUp extends Event {
    private int keyCode;

    public KeyUp(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
