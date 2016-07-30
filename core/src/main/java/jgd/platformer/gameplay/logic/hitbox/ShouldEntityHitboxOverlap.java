package jgd.platformer.gameplay.logic.hitbox;

import com.gempukku.secsy.entity.event.Event;

public class ShouldEntityHitboxOverlap extends Event {
    private boolean cancelled;

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
