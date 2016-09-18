package jgd.platformer.gameplay.rendering.model.shape;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface RenderedShapeComponent extends Component {
    String getModelPrefab();

    default Vector3 getTranslate() {
        return new Vector3();
    }

    default Vector3 getScale() {
        return new Vector3(1, 1, 1);
    }
}
