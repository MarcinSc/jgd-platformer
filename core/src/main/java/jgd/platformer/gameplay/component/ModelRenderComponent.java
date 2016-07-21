package jgd.platformer.gameplay.component;

import com.gempukku.secsy.entity.Component;

public interface ModelRenderComponent extends Component {
    String getModelPrefab();

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();
}
