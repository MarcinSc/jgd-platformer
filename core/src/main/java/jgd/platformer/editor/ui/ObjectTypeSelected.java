package jgd.platformer.editor.ui;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.event.Event;

public class ObjectTypeSelected extends Event {
    private String prefabName;
    private Vector3 renderSize;
    private Vector3 renderTranslate;
    private Vector3 placementTranslate;

    public ObjectTypeSelected(String prefabName, Vector3 renderSize, Vector3 renderTranslate, Vector3 placementTranslate) {
        this.prefabName = prefabName;
        this.renderSize = renderSize;
        this.renderTranslate = renderTranslate;
        this.placementTranslate = placementTranslate;
    }

    public String getPrefabName() {
        return prefabName;
    }

    public Vector3 getPlacementTranslate() {
        return placementTranslate;
    }

    public Vector3 getRenderSize() {
        return renderSize;
    }

    public Vector3 getRenderTranslate() {
        return renderTranslate;
    }
}
