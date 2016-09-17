package jgd.platformer.gameplay.rendering.model.color;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface ModelColoredCuboidComponent extends Component {
    Color getColor();

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();

    default float getScaleX() {
        return 1;
    }

    default float getScaleY() {
        return 1;
    }

    default float getScaleZ() {
        return 1;
    }
}
