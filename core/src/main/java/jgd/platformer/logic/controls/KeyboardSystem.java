package jgd.platformer.logic.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.logic.physics.ApplyPhysicsForces;

@RegisterSystem(
        profiles = "keyboard"
)
public class KeyboardSystem implements LifeCycleSystem {
    private int[] leftKeys = {Input.Keys.LEFT, Input.Keys.A};
    private int[] rightKeys = {Input.Keys.RIGHT, Input.Keys.D};

    @ReceiveEvent
    public void applyMovement(ApplyPhysicsForces event, EntityRef entity, PlayerControlledComponent playerControlled) {
        boolean leftPressed = isLeftPressed();
        boolean rightPressed = isRightPressed();

        float xDiff = 0;

        if (leftPressed && !rightPressed) {
            event.setBaseVelocityX(-playerControlled.getMovementVelocity());
        } else if (rightPressed && !leftPressed) {
            event.setBaseVelocityX(playerControlled.getMovementVelocity());
        } else {
            event.setBaseVelocityX(0);
        }
    }

    private boolean isLeftPressed() {
        return isAnyPressed(leftKeys);
    }

    private boolean isRightPressed() {
        return isAnyPressed(rightKeys);
    }

    private boolean isAnyPressed(int[] keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key))
                return true;
        }
        return false;
    }
}
