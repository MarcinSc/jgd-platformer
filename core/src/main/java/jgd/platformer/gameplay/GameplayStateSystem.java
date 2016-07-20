package jgd.platformer.gameplay;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.level.AfterLevelLoaded;
import jgd.platformer.gameplay.logic.health.PlayerDeath;
import jgd.platformer.gameplay.logic.health.PlayerWithoutLives;
import jgd.platformer.gameplay.logic.portal.LevelCompleted;
import jgd.platformer.gameplay.player.AfterPlayerCreated;

@RegisterSystem(
        profiles = "gameplay",
        shared = GameplayState.class
)
public class GameplayStateSystem implements GameplayState {
    private boolean levelFinished = false;
    private boolean playerDead = false;
    private boolean playerWithoutLives = false;

    @Override
    public boolean isLevelFinished() {
        return levelFinished;
    }

    @Override
    public boolean isPlayerDead() {
        return playerDead;
    }

    @Override
    public boolean isPlayerWithoutLives() {
        return playerWithoutLives;
    }

    @ReceiveEvent
    public void playedDied(PlayerDeath event, EntityRef entity) {
        playerDead = true;
    }

    @ReceiveEvent
    public void playerRanOutOfLives(PlayerWithoutLives event, EntityRef entity) {
        playerWithoutLives = true;
    }

    @ReceiveEvent
    public void levelCompleted(LevelCompleted event, EntityRef entity) {
        levelFinished = true;
    }

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entity) {
        levelFinished = false;
        playerDead = false;
    }

    @ReceiveEvent
    public void playerCreated(AfterPlayerCreated event, EntityRef entity) {
        playerDead = false;
        playerWithoutLives = false;
    }
}
