package jgd.platformer.logic.portal;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.logic.PlayerComponent;
import jgd.platformer.logic.hitbox.HitboxOverlapEvent;

@RegisterSystem(profiles = "gameplay")
public class LevelExitSystem {
    @ReceiveEvent
    public void playerEnteredPortal(HitboxOverlapEvent event, EntityRef entity, PlayerComponent player) {
        if (event.getOtherEntity().hasComponent(LevelExitPortalComponent.class)) {
            System.out.println("Player exited");
        }
    }
}
