package jgd.platformer.gameplay.logic.physics;

import com.gempukku.secsy.entity.event.Event;

public class EntityMovementProcessed extends Event {
    private boolean oldGrounded;
    private boolean newGrounded;
    private float oldLocationX;
    private float newLocationX;
    private float oldLocationY;
    private float newLocationY;

    public EntityMovementProcessed(boolean oldGrounded, float oldLocationX, float oldLocationY, boolean newGrounded, float newLocationX, float newLocationY) {
        this.oldGrounded = oldGrounded;
        this.oldLocationX = oldLocationX;
        this.oldLocationY = oldLocationY;
        this.newGrounded = newGrounded;
        this.newLocationX = newLocationX;
        this.newLocationY = newLocationY;
    }

    public boolean isNewGrounded() {
        return newGrounded;
    }

    public float getNewLocationX() {
        return newLocationX;
    }

    public float getNewLocationY() {
        return newLocationY;
    }

    public boolean isOldGrounded() {
        return oldGrounded;
    }

    public float getOldLocationX() {
        return oldLocationX;
    }

    public float getOldLocationY() {
        return oldLocationY;
    }
}
