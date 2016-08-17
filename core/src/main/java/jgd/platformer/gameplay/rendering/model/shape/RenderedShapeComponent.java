package jgd.platformer.gameplay.rendering.model.shape;

import com.gempukku.secsy.entity.Component;

public interface RenderedShapeComponent extends Component {
    String getModelPrefab();

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
