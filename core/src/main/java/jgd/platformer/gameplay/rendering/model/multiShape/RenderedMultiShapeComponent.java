package jgd.platformer.gameplay.rendering.model.multiShape;

import com.gempukku.secsy.entity.Component;

public interface RenderedMultiShapeComponent extends Component {
    String getModelPrefab();

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();

    int getShapeCount();

    float getShapeAdvanceX();

    float getShapeAdvanceY();

    float getShapeAdvanceZ();
}
