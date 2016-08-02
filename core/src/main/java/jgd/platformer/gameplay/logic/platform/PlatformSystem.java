package jgd.platformer.gameplay.logic.platform;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentUpdated;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapManager;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = "gameplay"
)
public class PlatformSystem {
    @Inject
    private HitboxOverlapManager hitboxOverlapManager;

    @ReceiveEvent
    public void afterPlatformMoved(AfterComponentUpdated event, EntityRef entityRef, PlatformComponent platform, LocationComponent location) {
        LocationComponent oldLocation = event.getOldComponent(LocationComponent.class);

        float deltaX = location.getX() - oldLocation.getX();
        float deltaY = location.getY() - oldLocation.getY();
        float deltaZ = location.getZ() - oldLocation.getZ();

        if (deltaX != 0 || deltaY != 0 || deltaZ != 0) {
            Rectangle2D.Float platformRectangle = new Rectangle2D.Float(
                    oldLocation.getX() + platform.getTranslateX(),
                    oldLocation.getY() + platform.getTranslateY(),
                    platform.getWidth(), platform.getHeight());

            for (EntityRef entityToMove : hitboxOverlapManager.findOverlappedEntities(platformRectangle, entity -> entity.hasComponent(PlatformMovableComponent.class))) {
                LocationComponent entityLocation = entityToMove.getComponent(LocationComponent.class);
                entityLocation.setX(entityLocation.getX() + deltaX);
                entityLocation.setY(entityLocation.getY() + deltaY);
                entityLocation.setZ(entityLocation.getZ() + deltaZ);
                entityToMove.saveChanges();
            }
        }
    }
}
