package jgd.platformer.gameplay;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.level.AfterLevelLoaded;
import jgd.platformer.gameplay.logic.portal.LevelCompleted;
import jgd.platformer.gameplay.player.AfterPlayerCreated;

@RegisterSystem(
        profiles = "gameplay",
        shared = GameplayState.class
)
public class GameplayStateSystem implements GameplayState {
    private boolean levelFinished = false;
    private boolean playedDead = false;

    @Override
    public boolean isLevelFinished() {
        return levelFinished;
    }

    @Override
    public boolean isPlayerDead() {
        return playedDead;
    }

    @ReceiveEvent
    public void playedDied(PlayerDeath event, EntityRef entity) {
        playedDead = true;
    }

    @ReceiveEvent
    public void levelCompleted(LevelCompleted event, EntityRef entity) {
        levelFinished = true;
    }

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entity) {
        levelFinished = false;
    }

    @ReceiveEvent
    public void playerCreated(AfterPlayerCreated event, EntityRef entity) {
        playedDead = false;
    }
}
