package jgd.platformer.gameplay.rendering.model.color;

import com.gempukku.secsy.entity.Component;

public interface ModelColoredCuboidComponent extends Component {
    int getRed();

    int getGreen();

    int getBlue();

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
