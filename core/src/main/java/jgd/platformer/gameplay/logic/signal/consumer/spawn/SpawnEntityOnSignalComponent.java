package jgd.platformer.gameplay.logic.signal.consumer.spawn;

import com.gempukku.secsy.entity.Component;

public interface SpawnEntityOnSignalComponent extends Component {
    String getPrefabName();

    float getDistanceX();

    float getDistanceY();

    float getDistanceZ();
}
