package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class MouseMoved extends Event {
    private int screenX;
    private int screenY;

    public MouseMoved(int screenX, int screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }
}
