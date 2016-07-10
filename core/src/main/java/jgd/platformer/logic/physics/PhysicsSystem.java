package jgd.platformer.logic.physics;

import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.component.LocationComponent;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        shared = PhysicsEngine.class
)
public class PhysicsSystem implements PhysicsEngine, LifeCycleSystem {
    private static final float HORIZONTAL_COLLIDER_VERTICAL_SPACE = 1 / 16f;
    private static final float HORIZONTAL_COLLIDER_WIDTH = 1 / 16f;
    private static final float VERTICAL_COLLIDER_HEIGHT = 1 / 16f;

    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex kineticObjectEntities;

    @Override
    public void initialize() {
        kineticObjectEntities = entityIndexManager.addIndexOnComponents(KineticObjectComponent.class, LocationComponent.class);
    }

    @Override
    public void processPhysics() {
        float seconds = timeManager.getTimeSinceLastUpdate() / 1000f;

        calculateNewVelocityAndAcceleration(seconds);

        for (EntityRef kineticEntity : kineticObjectEntities.getEntities()) {
            KineticObjectComponent kineticObject = kineticEntity.getComponent(KineticObjectComponent.class);
            float velocityX = kineticObject.getVelocityX();
            float velocityY = kineticObject.getVelocityY();

            LocationComponent location = kineticEntity.getComponent(LocationComponent.class);

            // s = v*t
            float newLocationX = location.getX() + velocityX * seconds;
            float newLocationY = location.getY() + velocityY * seconds;

            CollidingObjectComponent collidingObject = kineticEntity.getComponent(CollidingObjectComponent.class);
            if (collidingObject != null) {
                newLocationY = resolveVerticalCollisions(kineticEntity, collidingObject, kineticObject, velocityY, newLocationX, newLocationY);
                newLocationX = resolveHorizontalCollisions(kineticEntity, collidingObject, kineticObject, velocityX, newLocationX, newLocationY);
            }

            location.setX(newLocationX);
            location.setY(newLocationY);

            kineticEntity.saveChanges();
        }
    }

    private float resolveHorizontalCollisions(EntityRef kineticEntity, CollidingObjectComponent collidingObject, KineticObjectComponent kineticObject, float velocityX, float locationX, float locationY) {
        if (velocityX > 0) {
            // Need to check right bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + collidingObject.getTranslateX() + collidingObject.getWidth() * (1 - HORIZONTAL_COLLIDER_WIDTH),
                    locationY + collidingObject.getTranslateY() + collidingObject.getHeight() * HORIZONTAL_COLLIDER_VERTICAL_SPACE,
                    collidingObject.getWidth() * HORIZONTAL_COLLIDER_WIDTH,
                    collidingObject.getHeight() * (1 - 2 * HORIZONTAL_COLLIDER_VERTICAL_SPACE));

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, GetCollisionPoint.Direction.RIGHT);
            kineticEntity.send(getCollisionPoint);

            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                kineticObject.setVelocityX(0);
                return getCollisionPoint.getNearestCollisionPoint() - collidingObject.getTranslateX() - collidingObject.getWidth();
            }
        } else if (velocityX < 0) {
            // Need to check left bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + collidingObject.getTranslateX(),
                    locationY + collidingObject.getTranslateY() + collidingObject.getHeight() * HORIZONTAL_COLLIDER_VERTICAL_SPACE,
                    collidingObject.getWidth() * HORIZONTAL_COLLIDER_WIDTH,
                    collidingObject.getHeight() * (1 - 2 * HORIZONTAL_COLLIDER_VERTICAL_SPACE));

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, GetCollisionPoint.Direction.LEFT);
            kineticEntity.send(getCollisionPoint);

            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                kineticObject.setVelocityX(0);
                return getCollisionPoint.getNearestCollisionPoint() - collidingObject.getTranslateX();
            }
        }

        return locationX;
    }

    private float resolveVerticalCollisions(EntityRef kineticEntity, CollidingObjectComponent collidingObject, KineticObjectComponent kineticObject, float velocityY, float locationX, float locationY) {
        if (velocityY > 0) {
            // Need to check upper bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + collidingObject.getTranslateX() + collidingObject.getWidth() * HORIZONTAL_COLLIDER_WIDTH,
                    locationY + collidingObject.getTranslateY() + collidingObject.getHeight() * (1 - VERTICAL_COLLIDER_HEIGHT),
                    collidingObject.getWidth() * (1 - 2 * HORIZONTAL_COLLIDER_WIDTH),
                    collidingObject.getHeight() * VERTICAL_COLLIDER_HEIGHT);

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, GetCollisionPoint.Direction.UP);
            kineticEntity.send(getCollisionPoint);

            kineticObject.setGrounded(false);
            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                kineticObject.setVelocityY(0);
                return getCollisionPoint.getNearestCollisionPoint() - collidingObject.getTranslateY() - collidingObject.getHeight();
            }
        } else if (velocityY < 0) {
            // Need to check lower bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + collidingObject.getTranslateX() + collidingObject.getWidth() * HORIZONTAL_COLLIDER_WIDTH,
                    locationY + collidingObject.getTranslateY(),
                    collidingObject.getWidth() * (1 - 2 * HORIZONTAL_COLLIDER_WIDTH),
                    collidingObject.getHeight() * VERTICAL_COLLIDER_HEIGHT);

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, GetCollisionPoint.Direction.DOWN);
            kineticEntity.send(getCollisionPoint);

            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                kineticObject.setVelocityY(0);
                kineticObject.setGrounded(true);
                return getCollisionPoint.getNearestCollisionPoint() - collidingObject.getTranslateY();
            } else {
                kineticObject.setGrounded(false);
            }
        }

        return locationY;
    }

    private void calculateNewVelocityAndAcceleration(float seconds) {
        for (EntityRef kineticEntity : kineticObjectEntities.getEntities()) {
            KineticObjectComponent kineticObject = kineticEntity.getComponent(KineticObjectComponent.class);

            ApplyPhysicsForces applyForces = new ApplyPhysicsForces(kineticObject.getVelocityX(), kineticObject.getVelocityY());
            kineticEntity.send(applyForces);

            float oldAccelerationX = kineticObject.getAccelerationX();
            float oldAccelerationY = kineticObject.getAccelerationY();
            //   F = ma => a = F/m => [m=1] a = F
            float newAccelerationX = applyForces.getForceX();
            float newAccelerationY = applyForces.getForceY();
            float avgAccelerationX = (oldAccelerationX + newAccelerationX) / 2f;
            float avgAccelerationY = (oldAccelerationY + newAccelerationY) / 2f;

            // v = v0 + at
            float newVelocityX = applyForces.getBaseVelocityX() + avgAccelerationX * seconds;
            float newVelocityY = applyForces.getBaseVelocityY() + avgAccelerationY * seconds;

            kineticObject.setAccelerationX(newAccelerationX);
            kineticObject.setAccelerationY(newAccelerationY);
            kineticObject.setVelocityX(newVelocityX);
            kineticObject.setVelocityY(newVelocityY);

            kineticEntity.saveChanges();
        }
    }
}