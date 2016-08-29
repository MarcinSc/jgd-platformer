package jgd.platformer.gameplay.logic.health;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class HealthSystem {
    @ReceiveEvent
    public void playerDied(PlayerDeath playerDeath, EntityRef entity, LivesComponent livesComponent) {
        int newLivesCount = livesComponent.getLivesCount() - 1;
        livesComponent.setLivesCount(newLivesCount);
        entity.saveChanges();

        if (newLivesCount == 0) {
            entity.send(new PlayerWithoutLives());
        }
    }
}
