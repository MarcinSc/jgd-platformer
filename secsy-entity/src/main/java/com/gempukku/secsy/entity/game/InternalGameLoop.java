package com.gempukku.secsy.entity.game;

public interface InternalGameLoop {
    void addInternalGameLoopListener(InternalGameLoopListener internalGameLoopListener);

    void removeInternalGameLooplListener(InternalGameLoopListener internalGameLoopListener);

    void processUpdate();
}
