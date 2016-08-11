package jgd.platformer.gameplay.rendering.model;

import com.gempukku.secsy.entity.Component;

public interface RenderedShapeComponent extends Component {
    String getModelPrefab();

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();
}
