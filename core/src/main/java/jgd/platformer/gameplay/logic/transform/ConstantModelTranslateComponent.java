package jgd.platformer.gameplay.logic.transform;

import com.gempukku.secsy.entity.Component;

public interface ConstantModelTranslateComponent extends Component {
    long getPhaseShift();

    int getBeforeTime();

    int getMoveAwayTime();

    int getAwayTime();

    int getMoveBackTime();

    float getDistanceX();
    float getDistanceY();
    float getDistanceZ();

    String getInterpolationAway();

    String getInterpolationBack();
}
