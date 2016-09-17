package jgd.platformer.gameplay.logic.transform.activate;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface MoveActivatorComponent extends Component {
    Vector3 getDistance();

    long getMoveTime();
}
