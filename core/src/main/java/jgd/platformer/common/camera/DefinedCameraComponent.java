package jgd.platformer.common.camera;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface DefinedCameraComponent extends Component {
    Vector3 getLocation();

    void setLocation(Vector3 location);

    Vector3 getLookAt();

    void setLookAt(Vector3 lookAt);

    Vector3 getUp();

    void setUp(Vector3 up);
}
