package jgd.platformer.gameplay.logic.physics;

import com.gempukku.secsy.entity.event.Event;

public class ShouldProcessPhysics extends Event {
    private boolean cancelled;

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
