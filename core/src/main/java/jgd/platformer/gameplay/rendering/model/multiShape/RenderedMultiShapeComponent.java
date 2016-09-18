package jgd.platformer.gameplay.rendering.model.multiShape;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface RenderedMultiShapeComponent extends Component {
    String getModelPrefab();

    default Vector3 getTranslate() {
        return new Vector3();
    }

    Vector3 getShapeAdvance();

    int getShapeCount();
}
