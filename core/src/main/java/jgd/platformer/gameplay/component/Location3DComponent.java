package jgd.platformer.gameplay.component;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface Location3DComponent extends Component {
    Vector3 getLocation();

    void setLocation(Vector3 location);
}
