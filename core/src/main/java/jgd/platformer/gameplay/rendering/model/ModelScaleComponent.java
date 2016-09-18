package jgd.platformer.gameplay.rendering.model;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface ModelScaleComponent extends Component {
    default Vector3 getScale() {
        return new Vector3(1, 1, 1);
    }

    void setScale(Vector3 scale);
}
