package com.gempukku.gaming.rendering;

import com.gempukku.secsy.entity.event.Event;

public class ScreenResized extends Event {
    private int width;
    private int height;

    public ScreenResized(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
