package jgd.platformer.gameplay.logic.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface RectangleHitboxComponent extends Component {
    Vector2 getTranslate();

    Vector2 getSize();
}
