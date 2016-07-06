package jgd.platformer.logic.physics;

import com.gempukku.secsy.entity.event.Event;

public class ApplyPhysicsForces extends Event {
    private float baseVelocityX;
    private float baseVelocityY;

    private float forceX;
    private float forceY;

    public ApplyPhysicsForces(float baseVelocityX, float baseVelocityY) {
        this.baseVelocityX = baseVelocityX;
        this.baseVelocityY = baseVelocityY;
    }

    public float getBaseVelocityX() {
        return baseVelocityX;
    }

    public void setBaseVelocityX(float baseVelocityX) {
        this.baseVelocityX = baseVelocityX;
    }

    public float getBaseVelocityY() {
        return baseVelocityY;
    }

    public void setBaseVelocityY(float baseVelocityY) {
        this.baseVelocityY = baseVelocityY;
    }

    public float getForceX() {
        return forceX;
    }

    public float getForceY() {
        return forceY;
    }

    public void addForceX(float force) {
        forceX += force;
    }

    public void addForceY(float force) {
        forceY += force;
    }
}
