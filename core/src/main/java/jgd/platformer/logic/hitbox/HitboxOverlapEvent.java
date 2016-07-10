package jgd.platformer.logic.hitbox;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class HitboxOverlapEvent extends Event {
    private EntityRef otherEntity;

    public HitboxOverlapEvent(EntityRef otherEntity) {
        this.otherEntity = otherEntity;
    }

    public EntityRef getOtherEntity() {
        return otherEntity;
    }
}
