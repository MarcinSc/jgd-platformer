package jgd.platformer.gameplay.logic.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.audio.AudioManager;
import jgd.platformer.gameplay.logic.physics.ApplyPhysicsForces;
import jgd.platformer.gameplay.logic.physics.KineticObjectComponent;

@RegisterSystem(
        profiles = {"gameplay", "keyboard"}
)
public class KeyboardSystem implements LifeCycleSystem {
    @Inject
    private AudioManager audioManager;

    private Sound jumpSound;

    private int[] leftKeys = {Input.Keys.LEFT, Input.Keys.A};
    private int[] rightKeys = {Input.Keys.RIGHT, Input.Keys.D};
    private int[] jumpKeys = {Input.Keys.SPACE};

    @Override
    public void initialize() {
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_jump.ogg"));
    }

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
                audioManager.playSound(jumpSound);
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
