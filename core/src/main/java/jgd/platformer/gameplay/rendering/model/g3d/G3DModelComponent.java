package jgd.platformer.gameplay.rendering.model.g3d;

import com.gempukku.secsy.entity.Component;

public interface G3DModelComponent extends Component {
    String getModelFile();

    float getScaleX();

    float getScaleY();

    float getScaleZ();

    default float getOpacity() {
        return 1;
    }
}
