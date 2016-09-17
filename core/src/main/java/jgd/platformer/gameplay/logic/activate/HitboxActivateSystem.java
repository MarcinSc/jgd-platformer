package jgd.platformer.gameplay.logic.activate;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.controls.PlayerActivated;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapManager;
import jgd.platformer.gameplay.logic.hitbox.RectangleHitboxComponent;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class HitboxActivateSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private HitboxOverlapManager hitboxOverlapManager;

    @ReceiveEvent
    public void playerActivated(PlayerActivated event, EntityRef entityRef) {
        Iterable<EntityRef> players = entityManager.getEntitiesWithComponents(ActivatorComponent.class, RectangleHitboxComponent.class, Location3DComponent.class);
        for (EntityRef player : players) {
            RectangleHitboxComponent rectangleHitbox = player.getComponent(RectangleHitboxComponent.class);
            Vector3 location = player.getComponent(Location3DComponent.class).getLocation();

            Rectangle2D.Float rectangle = new Rectangle2D.Float(
                    location.x + rectangleHitbox.getTranslateX(),
                    location.y + rectangleHitbox.getTranslateY(),
                    rectangleHitbox.getWidth(),
                    rectangleHitbox.getHeight());

            int zLayer = MathUtils.floor(location.z);

            for (EntityRef activateEntity : hitboxOverlapManager.findOverlappedEntities(rectangle, zLayer,
                    entity -> entity.hasComponent(HitboxActivateComponent.class))) {
                activateEntity.send(new ActivateEntity(player));
            }
        }
    }
}
