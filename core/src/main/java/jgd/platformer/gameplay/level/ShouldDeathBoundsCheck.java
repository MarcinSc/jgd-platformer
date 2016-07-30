package jgd.platformer.gameplay.level;

import com.gempukku.secsy.entity.event.Event;

public class ShouldDeathBoundsCheck extends Event {
    private boolean cancelled;

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}