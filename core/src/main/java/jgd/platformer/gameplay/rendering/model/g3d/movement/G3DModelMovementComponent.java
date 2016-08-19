package jgd.platformer.gameplay.rendering.model.g3d.movement;

import com.gempukku.secsy.entity.Component;

public interface G3DModelMovementComponent extends Component {
    String getIdleAnimation();

    default float getIdleAnimationSpeed() {
        return 1f;
    }

    String getWalkAnimation();

    default float getWalkAnimationSpeed() {
        return 1f;
    }
}
