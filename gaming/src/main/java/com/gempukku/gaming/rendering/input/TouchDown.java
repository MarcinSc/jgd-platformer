package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class TouchDown extends Event {
    private int screenX;
    private int screenY;
    private int pointer;
    private int button;

    public TouchDown(int screenX, int screenY, int pointer, int button) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.pointer = pointer;
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public int getPointer() {
        return pointer;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }
}
