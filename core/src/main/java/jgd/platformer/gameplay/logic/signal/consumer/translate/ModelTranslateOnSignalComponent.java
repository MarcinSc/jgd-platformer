package jgd.platformer.gameplay.logic.signal.consumer.translate;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface ModelTranslateOnSignalComponent extends Component {
    Vector3 getDistance();

    long getMoveTime();

    String getInterpolation();
}
