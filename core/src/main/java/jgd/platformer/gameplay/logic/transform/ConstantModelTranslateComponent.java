package jgd.platformer.gameplay.logic.transform;

import com.gempukku.secsy.entity.Component;

public interface ConstantModelTranslateComponent extends Component {
    long getStartTime();

    int getCycleTime();

    float getDistanceX();

    float getDistanceY();

    float getDistanceZ();
}
