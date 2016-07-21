package jgd.platformer.gameplay.logic.collectible;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class CollectibleCollected extends Event {
    private EntityRef collectible;

    public CollectibleCollected(EntityRef collectible) {
        this.collectible = collectible;
    }

    public EntityRef getCollectible() {
        return collectible;
    }
}
