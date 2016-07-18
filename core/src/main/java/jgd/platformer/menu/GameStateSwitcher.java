package jgd.platformer.menu;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.SECSyContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.GameplayState;
import jgd.platformer.gameplay.level.LevelLoader;
import jgd.platformer.gameplay.player.PlayerManager;
import jgd.platformer.menu.rendering.NewGameRequested;

@RegisterSystem(
        profiles = "menu",
        shared = GameState.class
)
public class GameStateSwitcher implements GameState {
    private enum State {
        SHOWING_MENU(true), START_NEW_GAME(false), PLAYING_GAME(false);

        private boolean showMenu;

        State(boolean showMenu) {
            this.showMenu = showMenu;
        }
    }

    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private State currentState = State.SHOWING_MENU;
    private int currentLevelIndex = 0;

    @ReceiveEvent
    public void newGameRequested(NewGameRequested newGameRequested, EntityRef entity) {
        currentState = State.START_NEW_GAME;
    }

    @Override
    public boolean shouldShowMenu(SECSyContext gameplayContext) {
        if (currentState == State.START_NEW_GAME) {
            currentLevelIndex = 0;

            PlayerManager playerManager = gameplayContext.getSystem(PlayerManager.class);
            playerManager.createPlayer();

            LevelLoader levelLoader = gameplayContext.getSystem(LevelLoader.class);
            levelLoader.loadLevel(getLevelName(currentLevelIndex));
            currentState = State.PLAYING_GAME;
        }
        if (currentState == State.PLAYING_GAME) {
            GameplayState gameplayState = gameplayContext.getSystem(GameplayState.class);
            if (gameplayState.isLevelFinished()) {
                currentLevelIndex++;

                LevelLoader levelLoader = gameplayContext.getSystem(LevelLoader.class);
                levelLoader.unloadLevel();
                if (currentLevelIndex < getLevelCount()) {
                    levelLoader.loadLevel(getLevelName(currentLevelIndex));
                } else {
                    PlayerManager playerManager = gameplayContext.getSystem(PlayerManager.class);
                    playerManager.removePlayer();

                    currentState = State.SHOWING_MENU;
                }
            } else if (gameplayState.isPlayerDead()) {
                PlayerManager playerManager = gameplayContext.getSystem(PlayerManager.class);
                playerManager.removePlayer();

                currentState = State.SHOWING_MENU;
            }
        }

        return currentState.showMenu;
    }

    private String getLevelName(int index) {
        EntityData levelSequence = prefabManager.getPrefabByName("levelSequence");
        LevelSequenceComponent levelSequenceComponent = entityManager.wrapEntityData(levelSequence).getComponent(LevelSequenceComponent.class);
        return levelSequenceComponent.getLevelNames().get(index);
    }

    private int getLevelCount() {
        EntityData levelSequence = prefabManager.getPrefabByName("levelSequence");
        LevelSequenceComponent levelSequenceComponent = entityManager.wrapEntityData(levelSequence).getComponent(LevelSequenceComponent.class);
        return levelSequenceComponent.getLevelNames().size();
    }
}
