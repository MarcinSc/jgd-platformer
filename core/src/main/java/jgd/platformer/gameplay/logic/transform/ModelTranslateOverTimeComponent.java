package jgd.platformer.gameplay.logic.transform;

import com.gempukku.secsy.entity.Component;

public interface ModelTranslateOverTimeComponent extends Component {
    float getSourceX();

    void setSourceX(float sourceX);

    float getSourceY();

    void setSourceY(float sourceY);

    float getSourceZ();

    void setSourceZ(float sourceZ);

    float getDestinationX();

    void setDestinationX(float destinationX);

    float getDestinationY();

    void setDestinationY(float destinationY);

    float getDestinationZ();

    void setDestinationZ(float destinationZ);

    long getStartTime();
    void setStartTime(long startTime);

    long getMoveTime();
    void setMoveTime(long moveTime);

    String getInterpolation();
    void setInterpolation(String interpolation);
}
