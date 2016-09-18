package jgd.platformer.gameplay.logic.signal.consumer.spawn;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface SpawnEntityOnSignalComponent extends Component {
    String getPrefabName();

    Vector3 getDistance();
}
