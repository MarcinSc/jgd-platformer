package jgd.platformer.gameplay.rendering.model;

import com.gempukku.secsy.entity.Component;

public interface ModelScaleComponent extends Component {
    default float getScaleX() {
        return 1;
    }

    void setScaleX(float scaleX);

    default float getScaleY() {
        return 1;
    }

    void setScaleY(float scaleY);

    default float getScaleZ() {
        return 1;
    }

    void setScaleZ(float scaleZ);
}
