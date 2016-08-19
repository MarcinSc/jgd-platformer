package jgd.platformer.gameplay.logic.activate;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.controls.PlayerActivated;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapManager;
import jgd.platformer.gameplay.logic.hitbox.RectangleHitboxComponent;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = "gameplay"
)
public class HitboxActivateSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private HitboxOverlapManager hitboxOverlapManager;

    @ReceiveEvent
    public void playerActivated(PlayerActivated event, EntityRef entityRef) {
        Iterable<EntityRef> players = entityManager.getEntitiesWithComponents(PlayerComponent.class, RectangleHitboxComponent.class, LocationComponent.class);
        for (EntityRef player : players) {
            RectangleHitboxComponent rectangleHitbox = player.getComponent(RectangleHitboxComponent.class);
            LocationComponent location = player.getComponent(LocationComponent.class);

            Rectangle2D.Float rectangle = new Rectangle2D.Float(
                    location.getX() + rectangleHitbox.getTranslateX(),
                    location.getY() + rectangleHitbox.getTranslateY(),
                    rectangleHitbox.getWidth(),
                    rectangleHitbox.getHeight());

            for (EntityRef activateEntity : hitboxOverlapManager.findOverlappedEntities(rectangle,
                    entity -> entity.hasComponent(HitboxActivateComponent.class))) {
                activateEntity.send(new ActivateEntity());
            }
        }
    }
}
