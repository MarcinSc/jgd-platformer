package jgd.platformer.gameplay.logic.physics;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface CollisionObstacleComponent extends Component {
    default Vector2 getTranslate() {
        return new Vector2();
    }

    Vector2 getSize();

    List<String> getCollideSides();
}
