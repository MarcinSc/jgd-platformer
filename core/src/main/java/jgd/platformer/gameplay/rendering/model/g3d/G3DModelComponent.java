package jgd.platformer.gameplay.rendering.model.g3d;

import com.gempukku.secsy.entity.Component;

public interface G3DModelComponent extends Component {
    String getModelFile();

    default float getScaleX() {
        return 1;
    }

    default float getScaleY() {
        return 1;
    }

    default float getScaleZ() {
        return 1;
    }

    default float getOpacity() {
        return 1;
    }

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();
}
