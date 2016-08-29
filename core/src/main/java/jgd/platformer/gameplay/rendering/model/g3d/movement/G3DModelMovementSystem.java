package jgd.platformer.gameplay.rendering.model.g3d.movement;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.physics.ModelIdles;
import jgd.platformer.gameplay.logic.physics.ModelWalks;
import jgd.platformer.gameplay.rendering.model.g3d.PlayAnimation;

@RegisterSystem(
        profiles = "gameScreen"
)
public class G3DModelMovementSystem {
    @ReceiveEvent
    public void modelWalks(ModelWalks event, EntityRef entity, G3DModelMovementComponent movement) {
        entity.send(new PlayAnimation(movement.getWalkAnimation(), movement.getWalkAnimationSpeed(), 0.1f, -1));
    }

    @ReceiveEvent
    public void modelIdles(ModelIdles event, EntityRef entity, G3DModelMovementComponent movement) {
        entity.send(new PlayAnimation(movement.getIdleAnimation(), movement.getIdleAnimationSpeed(), 0.1f, -1));
    }
}
