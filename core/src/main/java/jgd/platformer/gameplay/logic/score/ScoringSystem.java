package jgd.platformer.gameplay.logic.score;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.collectible.CollectibleCollected;
import jgd.platformer.gameplay.logic.portal.LevelCompleted;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class ScoringSystem {
    @ReceiveEvent
    public void levelCompleted(LevelCompleted event, EntityRef entityRef, PlayerComponent player, ScoreComponent score) {
        score.setScore(score.getScore() + 1000);
        entityRef.saveChanges();
    }

    @ReceiveEvent
    public void scoringCollectibleCollected(CollectibleCollected event, EntityRef entityRef, ScoreComponent score) {
        if (event.getCollectible().hasComponent(ScoringCollectibleComponent.class)) {
            ScoringCollectibleComponent scoring = event.getCollectible().getComponent(ScoringCollectibleComponent.class);
            score.setScore(score.getScore() + scoring.getScoreIncrease());
            entityRef.saveChanges();
        }
    }
}
