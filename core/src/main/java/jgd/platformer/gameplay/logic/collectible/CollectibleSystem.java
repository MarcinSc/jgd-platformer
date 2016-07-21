package jgd.platformer.gameplay.logic.collectible;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapEvent;

@RegisterSystem
public class CollectibleSystem {
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void collectibleCollected(HitboxOverlapEvent event, EntityRef entity, CollectorComponent collector) {
        if (event.getOtherEntity().hasComponent(CollectibleComponent.class)) {
            entity.send(new CollectibleCollected(event.getOtherEntity()));
            entityManager.destroyEntity(event.getOtherEntity());
        }
    }
}
