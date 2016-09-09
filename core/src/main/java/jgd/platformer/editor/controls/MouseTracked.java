package jgd.platformer.editor.controls;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.event.Event;

public class MouseTracked extends Event {
    private Vector3 position;
    private boolean snap;

    public MouseTracked(Vector3 position, boolean snap) {
        this.position = position;
        this.snap = snap;
    }

    public Vector3 getPosition() {
        return position;
    }

    public boolean isSnap() {
        return snap;
    }
}
