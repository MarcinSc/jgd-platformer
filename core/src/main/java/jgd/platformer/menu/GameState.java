package jgd.platformer.menu;

import com.gempukku.secsy.context.SECSyContext;

public interface GameState {
    boolean shouldShowMenu(SECSyContext gameplayContext);
}
