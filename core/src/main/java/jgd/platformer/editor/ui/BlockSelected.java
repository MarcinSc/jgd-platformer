package jgd.platformer.editor.ui;

import com.gempukku.secsy.entity.event.Event;

public class BlockSelected extends Event {
    private String location;

    public BlockSelected(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
