package com.gempukku.secsy.entity.event;

public class ConsumableEvent extends Event {
    private boolean consumed;

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        consumed = true;
    }
}
