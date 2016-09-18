package jgd.platformer.gameplay.rendering.model.color;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface ModelColoredCuboidComponent extends Component {
    Color getColor();

    Vector3 getTranslate();

    default Vector3 getScale() {
        return new Vector3(1, 1, 1);
    }
}
