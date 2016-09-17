package jgd.platformer.gameplay.logic.transform;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface ModelTranslateOverTimeComponent extends Component {
    Vector3 getSource();

    void setSource(Vector3 source);

    Vector3 getDestination();

    void setDestination(Vector3 destination);

    long getStartTime();
    void setStartTime(long startTime);

    long getMoveTime();
    void setMoveTime(long moveTime);

    String getInterpolation();
    void setInterpolation(String interpolation);
}
