package jgd.platformer.gameplay.logic.signal.consumer;

import com.gempukku.secsy.entity.Component;

public interface ModelTranslateOnSignalComponent extends Component {
    float getDistanceX();

    float getDistanceY();

    float getDistanceZ();

    long getMoveTime();

    String getInterpolation();
}
