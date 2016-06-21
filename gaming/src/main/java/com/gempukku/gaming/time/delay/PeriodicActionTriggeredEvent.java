package com.gempukku.gaming.time.delay;

import com.gempukku.secsy.entity.event.Event;

public class PeriodicActionTriggeredEvent extends Event {
    public final String actionId;

    public PeriodicActionTriggeredEvent(String actionId) {
        this.actionId = actionId;
    }
}
