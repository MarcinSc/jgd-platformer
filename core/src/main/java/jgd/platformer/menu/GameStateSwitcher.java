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
import jgd.platformer.gameplay.audio.AudioManager;
import jgd.platformer.gameplay.level.LevelLoader;
import jgd.platformer.gameplay.player.PlayerManager;
import jgd.platformer.menu.rendering.*;

@RegisterSystem(
        profiles = "menu",
        shared = GameState.class
)
public class GameStateSwitcher implements GameState {
    private enum State {
        SHOWING_MENU(Screen.MAIN_MENU),
        START_NEW_GAME(Screen.GAMEPLAY), PLAYING_GAME(Screen.GAMEPLAY),
        START_LEVEL_EDITOR(Screen.EDITOR), EDITING_LEVEL(Screen.EDITOR);

        private GameState.Screen screen;

        State(GameState.Screen screen) {
            this.screen = screen;
        }
    }

    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private State currentState = State.SHOWING_MENU;
    private int currentLevelIndex = 0;

    private MasterVolumeSet lastMasterVolumeEvent;
    private MusicVolumeSet lastMusicVolumeEvent;
    private FXVolumeSet lastFxVolumeEvent;

    @ReceiveEvent
    public void newGameRequested(NewGameRequested newGameRequested, EntityRef entity) {
        currentState = State.START_NEW_GAME;
    }

    @ReceiveEvent
    public void levelEditorRequested(LevelEditorRequested levelEditorRequested, EntityRef entity) {
        currentState = State.START_LEVEL_EDITOR;
    }

    @ReceiveEvent
    public void masterVolumeChanged(MasterVolumeSet event, EntityRef entity) {
        lastMasterVolumeEvent = event;
    }

    @ReceiveEvent
    public void musicVolumeChanged(MusicVolumeSet event, EntityRef entity) {
        lastMusicVolumeEvent = event;
    }

    @ReceiveEvent
    public void fxVolumeChanged(FXVolumeSet event, EntityRef entity) {
        lastFxVolumeEvent = event;
    }

    @Override
    public Screen getUsedScreen(SECSyContext gameplayContext, SECSyContext editorContext) {
        if (lastMasterVolumeEvent != null) {
            gameplayContext.getSystem(AudioManager.class).setMasterVolume(lastMasterVolumeEvent.getVolume());
            lastMasterVolumeEvent = null;
        }
        if (lastMusicVolumeEvent != null) {
            gameplayContext.getSystem(AudioManager.class).setMusicVolume(lastMusicVolumeEvent.getVolume());
            lastMusicVolumeEvent = null;
        }
        if (lastFxVolumeEvent != null) {
            gameplayContext.getSystem(AudioManager.class).setFXVolume(lastFxVolumeEvent.getVolume());
            lastFxVolumeEvent = null;
        }

        if (currentState == State.START_NEW_GAME) {
            currentLevelIndex = 0;

            PlayerManager playerManager = gameplayContext.getSystem(PlayerManager.class);
            playerManager.createPlayer();

            LevelLoader levelLoader = gameplayContext.getSystem(LevelLoader.class);
            levelLoader.loadLevel(getLevelName(currentLevelIndex));
            currentState = State.PLAYING_GAME;
        }
        if (currentState == State.START_LEVEL_EDITOR) {
            LevelLoader levelLoader = editorContext.getSystem(LevelLoader.class);
            levelLoader.createNewLevel();
            currentState = State.EDITING_LEVEL;
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
            } else if (gameplayState.isPlayerWithoutLives()) {
                LevelLoader levelLoader = gameplayContext.getSystem(LevelLoader.class);
                levelLoader.unloadLevel();

                PlayerManager playerManager = gameplayContext.getSystem(PlayerManager.class);
                playerManager.removePlayer();

                currentState = State.SHOWING_MENU;
            } else if (gameplayState.isPlayerDead()) {
                LevelLoader levelLoader = gameplayContext.getSystem(LevelLoader.class);
                levelLoader.unloadLevel();
                levelLoader.loadLevel(getLevelName(currentLevelIndex));
            }
        }

        return currentState.screen;
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
