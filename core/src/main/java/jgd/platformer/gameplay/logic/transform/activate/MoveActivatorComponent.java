package jgd.platformer.gameplay.logic.transform.activate;

import com.gempukku.secsy.entity.Component;

public interface MoveActivatorComponent extends Component {
    float getDistanceX();

    float getDistanceY();

    float getDistanceZ();

    long getMoveTime();
}
