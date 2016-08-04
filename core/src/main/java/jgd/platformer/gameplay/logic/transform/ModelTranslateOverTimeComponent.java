package jgd.platformer.gameplay.logic.transform;

import com.gempukku.secsy.entity.Component;

public interface ModelTranslateOverTimeComponent extends Component {
    float getDistanceX();

    void setDistanceX(float distanceX);

    float getDistanceY();

    void setDistanceY(float distanceY);

    float getDistanceZ();

    void setDistanceZ(float distanceZ);

    long getStartTime();

    void setStartTime(long startTime);

    long getMoveTime();

    void setMoveTime(long moveTime);

    String getInterpolation();

    void setInterpolation(String interpolation);

    boolean isReverse();

    void setReverse(boolean reverse);
}
