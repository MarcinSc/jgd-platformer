package jgd.platformer.gameplay.logic.portal;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapEvent;

@RegisterSystem(profiles = {"gameScreen", "gameplay"})
public class LevelExitSystem {
    @ReceiveEvent
    public void playerEnteredPortal(HitboxOverlapEvent event, EntityRef entity, PlayerComponent player) {
        if (event.getOtherEntity().hasComponent(LevelExitPortalComponent.class)) {
            entity.send(new LevelCompleted());
        }
    }
}
