package jgd.platformer.gameplay.logic.activate;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class ActivateEntity extends Event {
    private EntityRef activator;

    public ActivateEntity(EntityRef activator) {
        this.activator = activator;
    }

    public EntityRef getActivator() {
        return activator;
    }
}
