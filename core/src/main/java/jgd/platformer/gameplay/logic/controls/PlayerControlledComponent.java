package jgd.platformer.gameplay.logic.controls;

import com.gempukku.secsy.entity.Component;

public interface PlayerControlledComponent extends Component {
    float getMovementVelocity();

    float getJumpVelocity();
}
