package jgd.platformer.logic.physics;

import com.gempukku.secsy.entity.event.Event;

import java.awt.geom.Rectangle2D;

public class GetCollisionPoint extends Event {
    enum Direction {
        RIGHT, LEFT, UP, DOWN
    }

    private Rectangle2D objectBounds;
    private Direction direction;
    private Float nearestCollisionPoint;

    public GetCollisionPoint(Rectangle2D objectBounds, Direction direction) {
        this.objectBounds = objectBounds;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Rectangle2D getObjectBounds() {
        return objectBounds;
    }

    public Float getNearestCollisionPoint() {
        return nearestCollisionPoint;
    }

    public void registerCollision(float collisionPoint) {
        if (((direction == Direction.RIGHT || direction == Direction.UP)
                && (nearestCollisionPoint == null || nearestCollisionPoint > collisionPoint))
                || ((direction == Direction.LEFT || direction == Direction.DOWN)
                && (nearestCollisionPoint == null || nearestCollisionPoint < collisionPoint))) {
            nearestCollisionPoint = collisionPoint;
        }
    }
}
