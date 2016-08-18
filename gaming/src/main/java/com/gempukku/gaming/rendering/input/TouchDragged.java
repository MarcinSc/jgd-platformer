package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class TouchDragged extends Event {
    private int screenX;
    private int screenY;
    private int pointer;

    public TouchDragged(int screenX, int screenY, int pointer) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.pointer = pointer;
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
