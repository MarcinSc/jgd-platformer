package jgd.platformer.menu;

import com.gempukku.secsy.context.SystemContext;

public interface GameState {
    enum Screen {
        MAIN_MENU, GAMEPLAY, EDITOR
    }

    Screen getUsedScreen(SystemContext gameplayContext, SystemContext editorContext);
}
