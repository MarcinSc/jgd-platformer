package jgd.platformer.editor;

import com.gempukku.secsy.entity.event.Event;
import com.gempukku.secsy.entity.io.EntityData;

public class RequestTestingLevel extends Event {
    private EntityData levelToTest;

    public RequestTestingLevel(EntityData levelToTest) {
        this.levelToTest = levelToTest;
    }

    public EntityData getLevelToTest() {
        return levelToTest;
    }
}
