package jgd.platformer.gameplay.rendering.model;

import com.gempukku.secsy.entity.Component;

public interface RotateModelOnMovementComponent extends Component {
    float getAngleLeft();

    float getAngleRight();
}
