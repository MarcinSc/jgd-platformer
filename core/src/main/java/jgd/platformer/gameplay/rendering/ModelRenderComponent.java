package jgd.platformer.gameplay.rendering;

import com.gempukku.secsy.entity.Component;

public interface ModelRenderComponent extends Component {
    String getModelPrefab();

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();
}
