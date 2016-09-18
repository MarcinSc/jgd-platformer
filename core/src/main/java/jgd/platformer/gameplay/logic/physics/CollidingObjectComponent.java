package jgd.platformer.gameplay.logic.physics;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface CollidingObjectComponent extends Component {
    Vector2 getTranslate();

    Vector2 getSize();
}
