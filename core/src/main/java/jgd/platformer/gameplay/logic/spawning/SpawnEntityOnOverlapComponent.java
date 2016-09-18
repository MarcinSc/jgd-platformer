package jgd.platformer.gameplay.logic.spawning;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface SpawnEntityOnOverlapComponent extends Component {
    String getPrefabName();

    Vector3 getDistance();
}
