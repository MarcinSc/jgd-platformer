package jgd.platformer.gameplay.logic.combat;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.faction.EnemyOverlapEvent;
import jgd.platformer.gameplay.logic.health.PlayerDeath;

@RegisterSystem(
        profiles = "gameplay"
)
public class CombatSystem {
    @ReceiveEvent
    public void playerOverlappingEnemy(EnemyOverlapEvent event, EntityRef entityRef, PlayerComponent player) {
        entityRef.send(new PlayerDeath());
    }
}
