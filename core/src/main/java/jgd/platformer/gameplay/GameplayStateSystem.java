package jgd.platformer.gameplay;

import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.gaming.time.delay.DelayManager;
import com.gempukku.gaming.time.delay.DelayedActionTriggeredEvent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.common.transition.ColorTintTransitionComponent;
import jgd.platformer.gameplay.level.AfterLevelLoaded;
import jgd.platformer.gameplay.level.ShouldDeathBoundsCheck;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.health.PlayerDeath;
import jgd.platformer.gameplay.logic.health.PlayerWithoutLives;
import jgd.platformer.gameplay.logic.hitbox.ShouldEntityHitboxOverlap;
import jgd.platformer.gameplay.logic.portal.LevelCompleted;
import jgd.platformer.gameplay.player.AfterPlayerCreated;

@RegisterSystem(
        profiles = "gameplay",
        shared = GameplayState.class
)
public class GameplayStateSystem implements GameplayState {
    private static final int FADE_DURATION = 200;
    @Inject
    private DelayManager delayManager;
    @Inject
    private RenderingEntityProvider renderingEntityProvider;
    @Inject
    private TimeManager timeManager;

    private boolean levelFinished = false;
    private boolean playerDead = false;
    private boolean playerWithoutLives = false;

    private boolean allowBoundsChecks = true;

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
        allowBoundsChecks = false;
        fadeOut();
        delayManager.addDelayedAction(entity, "playerDead", FADE_DURATION);
    }

    private void fadeOut() {
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
        ColorTintTransitionComponent transition = renderingEntity.createComponent(ColorTintTransitionComponent.class);
        transition.setStartTime(timeManager.getTime());
        transition.setLength(FADE_DURATION);
        transition.setFactorFrom(0);
        transition.setFactorTo(1);
        renderingEntity.saveChanges();
    }

    private void fadeIn() {
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
        ColorTintTransitionComponent transition = renderingEntity.createComponent(ColorTintTransitionComponent.class);
        transition.setStartTime(timeManager.getTime());
        transition.setLength(FADE_DURATION);
        transition.setFactorFrom(1);
        transition.setFactorTo(0);
        renderingEntity.saveChanges();
    }

    @ReceiveEvent
    public void delayedAction(DelayedActionTriggeredEvent event, EntityRef entityRef) {
        if (event.getActionId().equals("playerDead")) {
            playerDead = true;
        } else if (event.getActionId().equals("levelFinished")) {
            levelFinished = true;
        } else if (event.getActionId().equals("playerWithoutLives")) {
            playerWithoutLives = true;
        }
    }

    @ReceiveEvent
    public void hitboxCheckAllowed(ShouldEntityHitboxOverlap event, EntityRef entityRef, PlayerComponent player) {
        if (!allowBoundsChecks)
            event.cancel();
    }

    @ReceiveEvent
    public void deathBoundsCheckAllowed(ShouldDeathBoundsCheck event, EntityRef entityRef, PlayerComponent player) {
        if (!allowBoundsChecks)
            event.cancel();
    }

    @ReceiveEvent
    public void playerRanOutOfLives(PlayerWithoutLives event, EntityRef entity) {
        delayManager.addDelayedAction(entity, "playerWithoutLives", FADE_DURATION);
    }

    @ReceiveEvent
    public void levelCompleted(LevelCompleted event, EntityRef entity) {
        allowBoundsChecks = false;
        fadeOut();
        delayManager.addDelayedAction(entity, "levelFinished", FADE_DURATION);
    }

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entity) {
        allowBoundsChecks = true;
        levelFinished = false;
        playerDead = false;
        fadeIn();
    }

    @ReceiveEvent
    public void playerCreated(AfterPlayerCreated event, EntityRef entity) {
        playerDead = false;
        playerWithoutLives = false;
    }
}
