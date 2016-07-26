package jgd.platformer.gameplay.logic.spawning;

import com.gempukku.secsy.entity.Component;

public interface SpawnEntityOnOverlapComponent extends Component {
    String getPrefabName();

    float getDistanceX();

    float getDistanceY();

    float getDistanceZ();
}
