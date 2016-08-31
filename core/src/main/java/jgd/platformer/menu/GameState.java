package jgd.platformer.menu;

import com.gempukku.secsy.context.SECSyContext;

public interface GameState {
    enum Screen {
        MAIN_MENU, GAMEPLAY, EDITOR
    }

    Screen getUsedScreen(SECSyContext gameplayContext, SECSyContext editorContext);
}
