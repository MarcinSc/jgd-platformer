package jgd.platformer.gameplay.logic.signal.producer.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface HitboxOverlapSignalProducerComponent extends Component {
    Vector2 getTranslate();

    Vector2 getSize();
}
