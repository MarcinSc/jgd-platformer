package jgd.platformer.gameplay.logic.score;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.portal.LevelCompleted;

@RegisterSystem(
        profiles = "gameplay"
)
public class ScoringSystem {
    @ReceiveEvent
    public void levelCompleted(LevelCompleted event, EntityRef entityRef, PlayerComponent player, ScoreComponent score) {
        score.setScore(score.getScore() + 1000);
        entityRef.saveChanges();
    }
}
