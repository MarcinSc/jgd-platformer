package jgd.platformer.gameplay.logic.ai.combat;

import com.gempukku.secsy.entity.event.Event;

public class PerformAttack extends Event {
    private boolean success;

    public void succeed() {
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }
}
