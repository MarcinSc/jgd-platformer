package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class MouseScrolled extends Event {
    private int amount;

    public MouseScrolled(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
