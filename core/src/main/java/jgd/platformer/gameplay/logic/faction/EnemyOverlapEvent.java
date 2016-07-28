package jgd.platformer.gameplay.logic.faction;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class EnemyOverlapEvent extends Event {
    private EntityRef otherEntity;

    public EnemyOverlapEvent(EntityRef otherEntity) {
        this.otherEntity = otherEntity;
    }

    public EntityRef getOtherEntity() {
        return otherEntity;
    }
}
