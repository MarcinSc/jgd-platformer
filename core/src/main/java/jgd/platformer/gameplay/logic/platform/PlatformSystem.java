package jgd.platformer.gameplay.logic.platform;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentUpdated;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapManager;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class PlatformSystem {
    @Inject
    private HitboxOverlapManager hitboxOverlapManager;

    @ReceiveEvent
    public void afterPlatformMoved(AfterComponentUpdated event, EntityRef entityRef, PlatformComponent platform, Location3DComponent locationComponent) {
        Vector3 oldLocation = event.getOldComponent(Location3DComponent.class).getLocation();
        Vector3 location = locationComponent.getLocation();
        Vector2 translate = platform.getTranslate();
        Vector2 size = platform.getSize();

        float deltaX = location.x - oldLocation.x;
        float deltaY = location.y - oldLocation.y;
        float deltaZ = location.z - oldLocation.z;

        if (deltaX != 0 || deltaY != 0 || deltaZ != 0) {
            Rectangle2D.Float platformRectangle = new Rectangle2D.Float(
                    oldLocation.x + translate.x,
                    oldLocation.y + translate.y,
                    size.x, size.y);

            int zLayer = MathUtils.floor(location.z);

            for (EntityRef entityToMove : hitboxOverlapManager.findOverlappedEntities(platformRectangle, zLayer, entity -> entity.hasComponent(PlatformMovableComponent.class))) {
                Location3DComponent entityLocation = entityToMove.getComponent(Location3DComponent.class);
                entityLocation.setLocation(entityLocation.getLocation().add(deltaX, deltaY, deltaZ));
                entityToMove.saveChanges();
            }
        }
    }
}
