package jgd.platformer.gameplay.logic.physics;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface KineticObjectComponent extends Component {
    default Vector2 getVelocity() {
        return new Vector2();
    }

    void setVelocity(Vector2 velocity);

    default Vector2 getAcceleration() {
        return new Vector2();
    }

    void setAcceleration(Vector2 acceleration);

    boolean isGrounded();

    void setGrounded(boolean grounded);
}
