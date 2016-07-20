package jgd.platformer.gameplay.logic.health;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "gameplay"
)
public class HealthSystem {
    @ReceiveEvent
    public void playerDied(PlayerDeath playerDeath, EntityRef entity, LivesComponent livesComponent) {
        livesComponent.setLivesCount(livesComponent.getLivesCount() - 1);
        entity.saveChanges();

        if (livesComponent.getLivesCount() == 0) {
            entity.send(new PlayerWithoutLives());
        }
    }
}
