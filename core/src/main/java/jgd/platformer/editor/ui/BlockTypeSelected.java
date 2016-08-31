package jgd.platformer.editor.ui;

import com.gempukku.secsy.entity.event.Event;

public class BlockTypeSelected extends Event {
    private String prefabName;

    public BlockTypeSelected(String prefabName) {
        this.prefabName = prefabName;
    }

    public String getPrefabName() {
        return prefabName;
    }
}
