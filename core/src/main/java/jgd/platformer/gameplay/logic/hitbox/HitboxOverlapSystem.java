package jgd.platformer.gameplay.logic.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.AfterComponentUpdated;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import jgd.platformer.gameplay.component.LocationComponent;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@RegisterSystem(profiles = {"gameScreen", "gameplay"}, shared = HitboxOverlapManager.class)
public class HitboxOverlapSystem implements HitboxOverlapManager {
    private Map<EntityRef, Rectangle2D> hitboxEntities = new HashMap<>();

    @ReceiveEvent
    public void entityWithHitboxAdded(AfterComponentAdded event, EntityRef entity, RectangleHitboxComponent rectangleHitbox, LocationComponent location) {
        Rectangle2D shape = createShape(rectangleHitbox, location);
        hitboxEntities.put(entity, shape);
    }

    @ReceiveEvent
    public void entityWithHitboxModified(AfterComponentUpdated event, EntityRef entity, RectangleHitboxComponent rectangleHitbox, LocationComponent location) {
        Rectangle2D shape = createShape(rectangleHitbox, location);
        hitboxEntities.put(entity, shape);
    }

    @ReceiveEvent
    public void entityWithHitboxRemoved(BeforeComponentRemoved event, EntityRef entity, RectangleHitboxComponent rectangleHitbox, LocationComponent location) {
        hitboxEntities.remove(entity);
    }

    @ReceiveEvent
    public void checkOverlaps(GameLoopUpdate event, EntityRef entityRef) {
        List<OverlapEventToFire> eventsToFire = new LinkedList<>();

        for (Map.Entry<EntityRef, Rectangle2D> hitboxEntity : hitboxEntities.entrySet()) {
            EntityRef entity = hitboxEntity.getKey();
            Rectangle2D shape = hitboxEntity.getValue();

            ShouldEntityHitboxOverlap overlap = new ShouldEntityHitboxOverlap();
            entity.send(overlap);
            if (!overlap.isCancelled()) {
                int zLayer = MathUtils.floor(entity.getComponent(LocationComponent.class).getZ());
                for (Map.Entry<EntityRef, Rectangle2D> otherHitboxEntity : hitboxEntities.entrySet()) {
                    EntityRef otherEntity = otherHitboxEntity.getKey();
                    if (!entity.equals(otherEntity)) {
                        Rectangle2D otherShape = otherHitboxEntity.getValue();
                        int otherZLayer = MathUtils.floor(otherEntity.getComponent(LocationComponent.class).getZ());
                        if (zLayer == otherZLayer && shape.intersects(otherShape)) {
                            eventsToFire.add(new OverlapEventToFire(entity, new HitboxOverlapEvent(otherEntity)));
                        }
                    }
                }
            }
        }

        for (OverlapEventToFire overlapEventToFire : eventsToFire) {
            overlapEventToFire.entity.send(overlapEventToFire.hitboxOverlapEvent);
        }
    }

    @Override
    public Iterable<EntityRef> findOverlappedEntities(Rectangle2D rectangle, int zLayer, Predicate<EntityRef> filter) {
        List<EntityRef> result = new LinkedList<>();
        for (Map.Entry<EntityRef, Rectangle2D> hitboxEntity : hitboxEntities.entrySet()) {
            EntityRef entity = hitboxEntity.getKey();
            if (filter.test(entity)) {
                Rectangle2D shape = hitboxEntity.getValue();
                int entityZLayer = MathUtils.floor(entity.getComponent(LocationComponent.class).getZ());
                if (zLayer == entityZLayer && shape.intersects(rectangle))
                    result.add(entity);
            }
        }

        return result;
    }

    private Rectangle2D createShape(RectangleHitboxComponent rectangleHitbox, LocationComponent location) {
        return new Rectangle2D.Float(location.getX() + rectangleHitbox.getTranslateX(), location.getY() + rectangleHitbox.getTranslateY(),
                rectangleHitbox.getWidth(), rectangleHitbox.getHeight());
    }

    private static class OverlapEventToFire {
        private EntityRef entity;
        private HitboxOverlapEvent hitboxOverlapEvent;

        public OverlapEventToFire(EntityRef entity, HitboxOverlapEvent hitboxOverlapEvent) {
            this.entity = entity;
            this.hitboxOverlapEvent = hitboxOverlapEvent;
        }
    }
}
