package jgd.platformer.gameplay.logic.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.Location3DComponent;

import java.awt.geom.Rectangle2D;
import java.util.List;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class ModelCollider implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex collisionEntities;

    @Override
    public void initialize() {
        collisionEntities = entityIndexManager.addIndexOnComponents(CollisionObstacleComponent.class, Location3DComponent.class);
    }

    @ReceiveEvent
    public void getModelCollision(GetCollisionPoint event, EntityRef entityRef) {
        for (EntityRef collisionEntity : collisionEntities) {
            CollisionObstacleComponent obstacle = collisionEntity.getComponent(CollisionObstacleComponent.class);
            Vector3 location = collisionEntity.getComponent(Location3DComponent.class).getLocation();

            int zLayer = MathUtils.floor(location.z);

            List<String> sides = obstacle.getCollideSides();
            if (sides == null || containsDirection(sides, event.getDirection())) {
                Rectangle2D.Float rectangle = new Rectangle2D.Float(
                        location.x + obstacle.getTranslateX(),
                        location.y + obstacle.getTranslateY(),
                        obstacle.getWidth(), obstacle.getHeight());
                if (event.getZLayer() == zLayer && rectangle.intersects(event.getObjectBounds())) {
                    switch (event.getDirection()) {
                        case LEFT:
                            event.registerCollision((float) rectangle.getMaxX());
                            break;
                        case RIGHT:
                            event.registerCollision((float) rectangle.getMinX());
                            break;
                        case DOWN:
                            event.registerCollision((float) rectangle.getMaxY());
                            break;
                        case UP:
                            event.registerCollision((float) rectangle.getMinY());
                            break;
                    }
                }
            }
        }
    }

    private boolean containsDirection(List<String> sides, GetCollisionPoint.Direction direction) {
        for (String side : sides) {
            if (side.equals(direction.name()))
                return true;
        }
        return false;
    }
}
