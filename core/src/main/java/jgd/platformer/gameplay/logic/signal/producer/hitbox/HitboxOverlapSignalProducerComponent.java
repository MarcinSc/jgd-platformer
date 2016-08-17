package jgd.platformer.gameplay.logic.signal.producer.hitbox;

import com.gempukku.secsy.entity.Component;

public interface HitboxOverlapSignalProducerComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getWidth();

    float getHeight();
}
