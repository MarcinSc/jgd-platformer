package jgd.platformer.gameplay.logic.physics;

import com.gempukku.secsy.entity.Component;

public interface KineticObjectComponent extends Component {
    float getVelocityX();

    void setVelocityX(float velocityX);

    float getVelocityY();

    void setVelocityY(float velocityY);

    float getAccelerationX();

    void setAccelerationX(float accelerationX);

    float getAccelerationY();

    void setAccelerationY(float accelerationY);

    boolean isGrounded();

    void setGrounded(boolean grounded);
}
