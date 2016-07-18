package jgd.platformer.menu;

import com.gempukku.secsy.context.SECSyContext;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.level.LevelLoader;
import jgd.platformer.gameplay.player.PlayerManager;
import jgd.platformer.menu.rendering.NewGameRequested;

@RegisterSystem(
        profiles = "menu",
        shared = GameState.class
)
public class GameStateSwitcher implements GameState {
    private boolean shouldShowMenu = true;
    private boolean playerCreated = false;
    private boolean levelLoaded = false;

    @ReceiveEvent
    public void newGameRequested(NewGameRequested newGameRequested, EntityRef entity) {
        shouldShowMenu = false;
    }

    @Override
    public boolean shouldShowMenu(SECSyContext gameplayContext) {
        if (!shouldShowMenu) {
            if (!playerCreated) {
                PlayerManager playerManager = gameplayContext.getSystem(PlayerManager.class);
                playerManager.createPlayer();
                playerCreated = true;
            }
            if (!levelLoaded) {
                LevelLoader levelLoader = gameplayContext.getSystem(LevelLoader.class);
                levelLoader.loadLevel("level-sample2");
                levelLoaded = true;
            }
        }
        return shouldShowMenu;
    }
}
