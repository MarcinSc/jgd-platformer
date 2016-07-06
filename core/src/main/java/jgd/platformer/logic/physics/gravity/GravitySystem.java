package jgd.platformer.logic.physics.gravity;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.logic.physics.ApplyPhysicsForces;

@RegisterSystem
public class GravitySystem {
    private static final float GRAVITY_FORCE = -9.81f;

    @ReceiveEvent
    public void applyGravity(ApplyPhysicsForces event, EntityRef entity, AffectedByGravityComponent affectedByGravity) {
        event.addForceY(GRAVITY_FORCE * affectedByGravity.getGravityMultiplier());
    }
}
