package jgd.platformer.editor;

import com.gempukku.secsy.entity.io.EntityData;

public interface EditorState {
    EntityData getLevelToTest();

    void consumeLevelToTest();
}
