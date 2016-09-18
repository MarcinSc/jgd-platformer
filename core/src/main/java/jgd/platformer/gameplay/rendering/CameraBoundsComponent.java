package jgd.platformer.gameplay.rendering;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface CameraBoundsComponent extends Component {
    Vector2 getMin();

    Vector2 getMax();
}
