package jgd.platformer.gameplay.level;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface PlayerSpawnComponent extends Component {
    Vector3 getLocation();
}
