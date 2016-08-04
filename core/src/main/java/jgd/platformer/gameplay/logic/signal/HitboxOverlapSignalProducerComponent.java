package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.entity.Component;

public interface HitboxOverlapSignalProducerComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getWidth();

    float getHeight();
}
