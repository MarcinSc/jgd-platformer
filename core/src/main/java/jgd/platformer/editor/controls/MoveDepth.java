package jgd.platformer.editor.controls;

import com.gempukku.secsy.entity.event.Event;

public class MoveDepth extends Event {
    private int depth;

    public MoveDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }
}
