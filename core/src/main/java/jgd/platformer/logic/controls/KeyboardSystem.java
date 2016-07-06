package jgd.platformer.logic.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.logic.physics.ApplyPhysicsForces;
import jgd.platformer.logic.physics.KineticObjectComponent;

@RegisterSystem(
        profiles = "keyboard"
)
public class KeyboardSystem implements LifeCycleSystem {
    private int[] leftKeys = {Input.Keys.LEFT, Input.Keys.A};
    private int[] rightKeys = {Input.Keys.RIGHT, Input.Keys.D};
    private int[] jumpKeys = {Input.Keys.SPACE};

    @ReceiveEvent
    public void applyMovement(ApplyPhysicsForces event, EntityRef entity, PlayerControlledComponent playerControlled, KineticObjectComponent kineticObject) {
        if (kineticObject.isGrounded()) {
            boolean leftPressed = isLeftPressed();
            boolean rightPressed = isRightPressed();

            if (leftPressed && !rightPressed) {
                event.setBaseVelocityX(-playerControlled.getMovementVelocity());
            } else if (rightPressed && !leftPressed) {
                event.setBaseVelocityX(playerControlled.getMovementVelocity());
            } else {
                event.setBaseVelocityX(0);
            }

            if (isAnyPressed(jumpKeys)) {
                event.setBaseVelocityY(playerControlled.getJumpVelocity());
            } else {
                event.setBaseVelocityY(0);
            }
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
