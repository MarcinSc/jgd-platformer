package jgd.platformer.editor.ui;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface ObjectInEditorComponent extends Component {
    String getDisplayName();

    Vector3 getRenderSize();

    Vector3 getRenderTranslate();

    default Vector3 getPlacementTranslate() {
        return new Vector3();
    }

    ;
}
