package jgd.platformer.gameplay.logic.platform;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface PlatformComponent extends Component {
    Vector2 getTranslate();

    Vector2 getSize();
}
