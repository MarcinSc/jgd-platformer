package jgd.platformer.editor;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}, shared = EditorState.class)
public class EditorStateSystem implements EditorState {
    private EntityData levelToTest;

    @ReceiveEvent
    public void levelTestRequested(RequestTestingLevel event, EntityRef entityRef) {
        levelToTest = event.getLevelToTest();
    }

    @Override
    public void consumeLevelToTest() {
        levelToTest = null;
    }

    @Override
    public EntityData getLevelToTest() {
        return levelToTest;
    }
}
