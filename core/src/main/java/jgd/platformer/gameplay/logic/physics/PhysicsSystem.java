package jgd.platformer.gameplay.logic.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.Location3DComponent;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"},
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
        kineticObjectEntities = entityIndexManager.addIndexOnComponents(KineticObjectComponent.class, Location3DComponent.class);
    }

    @Override
    public void processPhysics() {
        float seconds = timeManager.getTimeSinceLastUpdate() / 1000f;

        if (seconds > 0) {
            calculateNewVelocityAndAcceleration(seconds);

            for (EntityRef kineticEntity : kineticObjectEntities.getEntities()) {
                ShouldProcessPhysics shouldProcessPhysics = new ShouldProcessPhysics();
                kineticEntity.send(shouldProcessPhysics);

                if (!shouldProcessPhysics.isCancelled()) {
                    KineticObjectComponent kineticObject = kineticEntity.getComponent(KineticObjectComponent.class);
                    Vector2 velocity = kineticObject.getVelocity();
                    float velocityX = velocity.x;
                    float velocityY = velocity.y;

                    Location3DComponent locationComponent = kineticEntity.getComponent(Location3DComponent.class);
                    Vector3 location = locationComponent.getLocation();

                    int zLayer = MathUtils.floor(location.z);

                    boolean oldGrounded = kineticObject.isGrounded();
                    float oldLocationX = location.x;
                    float oldLocationY = location.y;

                    // s = v*t
                    float newLocationX = oldLocationX + velocityX * seconds;
                    float newLocationY = oldLocationY + velocityY * seconds;

                    CollidingObjectComponent collidingObject = kineticEntity.getComponent(CollidingObjectComponent.class);
                    if (collidingObject != null) {
                        newLocationY = resolveVerticalCollisions(kineticEntity, collidingObject, kineticObject, velocityY, newLocationX, newLocationY, zLayer);
                        newLocationX = resolveHorizontalCollisions(kineticEntity, collidingObject, kineticObject, velocityX, newLocationX, newLocationY, zLayer);
                    }

                    location.x = newLocationX;
                    location.y = newLocationY;
                    locationComponent.setLocation(location);

                    ShouldEntityMove shouldEntityMove = new ShouldEntityMove(oldGrounded, oldLocationX, oldLocationY, kineticObject.isGrounded(), newLocationX, newLocationY);
                    kineticEntity.send(shouldEntityMove);

                    if (!shouldEntityMove.isCancelled()) {
                        boolean newGrounded = kineticObject.isGrounded();
                        kineticEntity.saveChanges();
                        kineticEntity.send(new EntityMovementProcessed(oldGrounded, oldLocationX, oldLocationY, newGrounded, newLocationX, newLocationY));
                    }
                }
            }
        }
    }

    private float resolveHorizontalCollisions(EntityRef kineticEntity, CollidingObjectComponent collidingObject, KineticObjectComponent kineticObject, float velocityX, float locationX, float locationY, int zLayer) {
        if (velocityX > 0) {
            Vector2 translate = collidingObject.getTranslate();
            Vector2 size = collidingObject.getSize();
            // Need to check right bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + translate.x + size.x * (1 - HORIZONTAL_COLLIDER_WIDTH),
                    locationY + translate.y + size.y * HORIZONTAL_COLLIDER_VERTICAL_SPACE,
                    size.x * HORIZONTAL_COLLIDER_WIDTH,
                    size.y * (1 - 2 * HORIZONTAL_COLLIDER_VERTICAL_SPACE));

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, zLayer, GetCollisionPoint.Direction.RIGHT);
            kineticEntity.send(getCollisionPoint);

            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                Vector2 velocity = kineticObject.getVelocity();
                velocity.x = 0;
                kineticObject.setVelocity(velocity);
                return getCollisionPoint.getNearestCollisionPoint() - translate.x - size.x;
            }
        } else if (velocityX < 0) {
            Vector2 translate = collidingObject.getTranslate();
            Vector2 size = collidingObject.getSize();
            // Need to check left bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + translate.x,
                    locationY + translate.y + size.y * HORIZONTAL_COLLIDER_VERTICAL_SPACE,
                    size.x * HORIZONTAL_COLLIDER_WIDTH,
                    size.y * (1 - 2 * HORIZONTAL_COLLIDER_VERTICAL_SPACE));

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, zLayer, GetCollisionPoint.Direction.LEFT);
            kineticEntity.send(getCollisionPoint);

            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                Vector2 velocity = kineticObject.getVelocity();
                velocity.x = 0;
                kineticObject.setVelocity(velocity);
                return getCollisionPoint.getNearestCollisionPoint() - translate.x;
            }
        }

        return locationX;
    }

    private float resolveVerticalCollisions(EntityRef kineticEntity, CollidingObjectComponent collidingObject, KineticObjectComponent kineticObject, float velocityY, float locationX, float locationY, int zLayer) {
        if (velocityY > 0) {
            Vector2 translate = collidingObject.getTranslate();
            Vector2 size = collidingObject.getSize();
            // Need to check upper bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + translate.x + size.x * HORIZONTAL_COLLIDER_WIDTH,
                    locationY + translate.y + size.y * (1 - VERTICAL_COLLIDER_HEIGHT),
                    size.x * (1 - 2 * HORIZONTAL_COLLIDER_WIDTH),
                    size.y * VERTICAL_COLLIDER_HEIGHT);

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, zLayer, GetCollisionPoint.Direction.UP);
            kineticEntity.send(getCollisionPoint);

            kineticObject.setGrounded(false);
            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                Vector2 velocity = kineticObject.getVelocity();
                velocity.y = 0;
                kineticObject.setVelocity(velocity);
                return getCollisionPoint.getNearestCollisionPoint() - translate.y - size.y;
            }
        } else if (velocityY < 0) {
            Vector2 translate = collidingObject.getTranslate();
            Vector2 size = collidingObject.getSize();
            // Need to check lower bounds
            Rectangle2D.Float collisionBounds = new Rectangle2D.Float(
                    locationX + translate.x + size.x * HORIZONTAL_COLLIDER_WIDTH,
                    locationY + translate.y,
                    size.x * (1 - 2 * HORIZONTAL_COLLIDER_WIDTH),
                    size.y * VERTICAL_COLLIDER_HEIGHT);

            GetCollisionPoint getCollisionPoint = new GetCollisionPoint(collisionBounds, zLayer, GetCollisionPoint.Direction.DOWN);
            kineticEntity.send(getCollisionPoint);

            if (getCollisionPoint.getNearestCollisionPoint() != null) {
                Vector2 velocity = kineticObject.getVelocity();
                velocity.y = 0;
                kineticObject.setVelocity(velocity);
                kineticObject.setGrounded(true);
                return getCollisionPoint.getNearestCollisionPoint() - translate.y;
            } else {
                kineticObject.setGrounded(false);
            }
        }

        return locationY;
    }

    private void calculateNewVelocityAndAcceleration(float seconds) {
        for (EntityRef kineticEntity : kineticObjectEntities.getEntities()) {
            ShouldProcessPhysics shouldProcessPhysics = new ShouldProcessPhysics();
            kineticEntity.send(shouldProcessPhysics);

            if (!shouldProcessPhysics.isCancelled()) {
                KineticObjectComponent kineticObject = kineticEntity.getComponent(KineticObjectComponent.class);
                Vector2 velocity = kineticObject.getVelocity();
                Vector2 acceleration = kineticObject.getAcceleration();

                ApplyPhysicsForces applyForces = new ApplyPhysicsForces(velocity.x, velocity.y);
                kineticEntity.send(applyForces);

                float oldAccelerationX = acceleration.x;
                float oldAccelerationY = acceleration.y;
                //   F = ma => a = F/m => [m=1] a = F
                float newAccelerationX = applyForces.getForceX();
                float newAccelerationY = applyForces.getForceY();
                float avgAccelerationX = (oldAccelerationX + newAccelerationX) / 2f;
                float avgAccelerationY = (oldAccelerationY + newAccelerationY) / 2f;

                // v = v0 + at
                float newVelocityX = applyForces.getBaseVelocityX() + avgAccelerationX * seconds;
                float newVelocityY = applyForces.getBaseVelocityY() + avgAccelerationY * seconds;

                acceleration.set(newAccelerationX, newAccelerationY);
                kineticObject.setAcceleration(acceleration);
                velocity.set(newVelocityX, newVelocityY);
                kineticObject.setVelocity(velocity);

                kineticEntity.saveChanges();
            }
        }
    }
}
