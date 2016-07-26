package jgd.platformer.gameplay.logic.ai.movement;

import com.gempukku.gaming.ai.AIEngine;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.ai.FacingDirectionComponent;
import jgd.platformer.gameplay.logic.physics.ApplyPhysicsForces;
import jgd.platformer.gameplay.logic.physics.KineticObjectComponent;
import jgd.platformer.gameplay.logic.physics.ShouldEntityMove;

@RegisterSystem(
        profiles = "gameplay"
)
public class AIMovementSystem {
    @Inject
    private AIEngine aiEngine;

    @ReceiveEvent
    public void applyMovement(ApplyPhysicsForces event, EntityRef entityRef, KineticObjectComponent kineticObject,
                              AIMovementConfigurationComponent movementConfiguration, FacingDirectionComponent facingDirection) {
        if (kineticObject.isGrounded()) {
            if (entityRef.hasComponent(AIApplyMovementIfDoesNotFallComponent.class)) {
                if (facingDirection.getDirection().equals("right")) {
                    event.setBaseVelocityX(movementConfiguration.getMovementVelocity());
                } else {
                    event.setBaseVelocityX(-movementConfiguration.getMovementVelocity());
                }
            } else {
                event.setBaseVelocityX(0);
            }
        }
    }

    @ReceiveEvent
    public void shouldMove(ShouldEntityMove event, EntityRef entityRef, AIApplyMovementIfDoesNotFallComponent doesNotFall) {
        if (!event.isNewGrounded() && event.isOldGrounded()) {
            for (MoveInDirectionUntilCannotTask moveInDirectionUntilCannotTask : aiEngine.getRunningTasksOfType(entityRef, MoveInDirectionUntilCannotTask.class)) {
                moveInDirectionUntilCannotTask.notifyCantMove(aiEngine.getReference(entityRef));
            }

            event.cancel();
        }
    }
}
