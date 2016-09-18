package jgd.platformer.gameplay.rendering.model.g3d;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface G3DModelComponent extends Component {
    String getModelFile();

    default Vector3 getTranslate() {
        return new Vector3();
    }

    default Vector3 getScale() {
        return new Vector3(1, 1, 1);
    }

    default float getOpacity() {
        return 1;
    }
}
