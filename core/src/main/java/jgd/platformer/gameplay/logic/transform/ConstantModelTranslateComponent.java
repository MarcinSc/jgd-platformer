package jgd.platformer.gameplay.logic.transform;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface ConstantModelTranslateComponent extends Component {
    long getPhaseShift();

    int getBeforeTime();

    int getMoveAwayTime();

    int getAwayTime();

    int getMoveBackTime();

    Vector3 getDistance();

    String getInterpolationAway();

    String getInterpolationBack();
}
